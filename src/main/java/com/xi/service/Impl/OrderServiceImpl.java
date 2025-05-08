package com.xi.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xi.constant.RedisConstant;
import com.xi.constant.OrderTagConstant;
import com.xi.constant.TopicConstant;
import com.xi.convert.OrderConvert;
import com.xi.entity.tb.OrderDo;
import com.xi.entity.tb.UserAddrOrderDo;
import com.xi.entity.dto.BasketDto;
import com.xi.entity.dto.OrderDto;
import com.xi.entity.dto.UserAddrDto;
import com.xi.entity.param.OrderParam;
import com.xi.enums.ResponseCodeEnum;
import com.xi.exception.BizException;
import com.xi.mapper.OrderMapper;
import com.xi.service.*;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-28
 */
@Service
@Transactional
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderDo> implements OrderService {

    @Resource
    private SkuService skuService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private BasketService basketService;

    @Resource
    private UserAddrService userAddrService;

    @Resource
    private UserAddrOrderService userAddrOrderService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource(name = "secKillStockDecreaseThreadPool")
    private ExecutorService executorService;

    @Override
    public void SubmitOrder(OrderParam orderParam) {
        // 创建订单
        String orderSerialNumber = createOrderAndUserAddrOrder(orderParam);
        orderParam.setOrderSerialNumberList(Collections.singletonList(orderSerialNumber));
        orderParam.setTag(OrderTagConstant.ORDER_TAG_DIRECT_PURCHASE);

        // 发送半事务消息
        rocketMQTemplate.sendMessageInTransaction(
                TopicConstant.ORDER_CREATE_TOPIC,
                MessageBuilder.withPayload(orderParam).setHeader(MessageConst.PROPERTY_TAGS, OrderTagConstant.ORDER_TAG_DIRECT_PURCHASE).build(),
                null
        );

    }

    @Override
    public void submitBasketOrder(OrderParam orderParam) {
        List<BasketDto> basketDtoList = orderParam.getBasketDtoList();

        // Redis库存预检查
        for (BasketDto basketDto : basketDtoList) {
            if (skuService.getSkuDtoBySkuId(basketDto.getSkuId()).getStocks() < basketDto.getStocks()) {
                throw new BizException(ResponseCodeEnum.STOCKS_NOT_ENOUGH);
            }
        }

        // 创建订单
        List<String> orderSerialNumberList = createOrderAndUserAddrOrder(basketDtoList);
        orderParam.setOrderSerialNumberList(orderSerialNumberList);
        orderParam.setTag(OrderTagConstant.ORDER_TAG_BASKET_PURCHASE);

        // 发送半事务消息
        rocketMQTemplate.sendMessageInTransaction(
                TopicConstant.ORDER_CREATE_TOPIC,
                MessageBuilder.withPayload(orderParam).setHeader(MessageConst.PROPERTY_TAGS, OrderTagConstant.ORDER_TAG_BASKET_PURCHASE).build(),
                null
        );

    }

    @Override
    public void submitOrderInSecKill(OrderParam orderParam) {
        // 创建订单
        String orderSerialNumber = createOrderAndUserAddrOrder(orderParam);
        orderParam.setOrderSerialNumberList(Collections.singletonList(orderSerialNumber));
        orderParam.setTag(OrderTagConstant.ORDER_TAG_SEC_KILL_DIRECT_PURCHASE);

        // Redis原子扣减
        RScript script = redissonClient.getScript();

        String skuStocksDeduct = """
                local skuStocks = redis.call('HGET', KEYS[1], ARGV[1])
                if tonumber(skuStocks) >= tonumber(ARGV[2]) then
                    redis.call('HINCRBY', KEYS[1], ARGV[1], -ARGV[2])
                    redis.call("HINCRBY', KEYS[1], ARGV[3], 1)
                    return 1
                else
                    return 0
                end
                """;

        String sha1 = script.scriptLoad(skuStocksDeduct);
        Integer re = script.evalSha(
                RScript.Mode.READ_WRITE,
                sha1,
                RScript.ReturnType.INTEGER,
                Collections.singletonList(RedisConstant.getSkuKey(orderParam.getSkuId())),
                RedisConstant.STOCKS,
                orderParam.getProdCount(),
                RedisConstant.VERSION
        );

        // 预扣减失败
        if (re == 0) {
            throw new BizException(ResponseCodeEnum.STOCKS_NOT_ENOUGH);
        }

        // 发送半事务消息
        rocketMQTemplate.sendMessageInTransaction(
                TopicConstant.ORDER_CREATE_TOPIC,
                MessageBuilder.withPayload(orderParam).setHeader(MessageConst.PROPERTY_TAGS, OrderTagConstant.ORDER_TAG_SEC_KILL_DIRECT_PURCHASE).build(),
                null
        );

    }

    @Override
    public void submitBasketOrderInSecKill(OrderParam orderParam) {
        // 获取购物车列表
        List<BasketDto> basketDtoList = orderParam.getBasketDtoList();

        basketDtoList.forEach(basketDto -> basketDto.setDeal(true));

        // 库存预扣减
        List<CompletableFuture<Result>> futureList = basketDtoList.stream().map(basketDto -> CompletableFuture.supplyAsync(() ->
                stockDeduct(basketDto), executorService)).toList();

        List<BasketDto> successItems = new ArrayList<>();
        List<BasketDto> failureItems = new ArrayList<>();

        try {
            for (CompletableFuture<Result> future : futureList) {
                if (!future.get().success) {
                    failureItems.add(future.get().getBasketDto());
                    break;
                } else {
                    successItems.add(future.get().getBasketDto());
                }
            }
        } catch (Exception e) {
            // 取消所有未完成任务
            futureList.forEach(f -> f.cancel(true));
            throw new BizException(ResponseCodeEnum.SYSTEM_ERROR);
        }

        // 失败队列非空 回滚
        if (!failureItems.isEmpty()) {
            rollbackDeduction(successItems);
        }

        List<String> orderSerialNumberList = createOrderAndUserAddrOrder(orderParam.getBasketDtoList());
        orderParam.setOrderSerialNumberList(orderSerialNumberList);
        orderParam.setTag(OrderTagConstant.ORDER_TAG_SEC_KILL_BASKET_PURCHASE);

        // 发送半事务消息
        rocketMQTemplate.sendMessageInTransaction(
                TopicConstant.ORDER_CREATE_TOPIC,
                MessageBuilder.withPayload(orderParam).setHeader(MessageConst.PROPERTY_TAGS, OrderTagConstant.ORDER_TAG_SEC_KILL_BASKET_PURCHASE).build(),
                null
        );
    }

    /**
     * 库存扣减回滚
     *
     * @param successItems 扣减成功集合
     */
    private void rollbackDeduction(List<BasketDto> successItems) {
        RScript script = redissonClient.getScript();
        String skuStocksRollback = """
                redis.call('HINCRBY', KEYS[1], ARGV[1], ARGV[2])
                redis.call("HINCRBY', KEYS[1], ARGV[3], -1)
                """;
        String sha1 = script.scriptLoad(skuStocksRollback);
        for (BasketDto basketDto : successItems) {
            script.evalSha(RScript.Mode.READ_WRITE,
                    sha1,
                    RScript.ReturnType.INTEGER,
                    Collections.singletonList(RedisConstant.getSkuKey(basketDto.getSkuId())),
                    RedisConstant.STOCKS,
                    basketDto.getStocks(),
                    RedisConstant.VERSION
            );
        }
    }

    /**
     * 库存扣减
     *
     * @param basketDto 购物车参数
     * @return 扣减结果
     */
    private Result stockDeduct(BasketDto basketDto) {
        RScript script = redissonClient.getScript();

        String skuStocksDeduct = """
                local skuStocks = redis.call('HGET', KEYS[1], ARGV[1])
                if tonumber(skuStocks) >= tonumber(ARGV[2]) then
                    redis.call('HINCRBY', KEYS[1], ARGV[1], -ARGV[2])
                    redis.call("HINCRBY', KEYS[1], ARGV[3], 1)
                    return 1
                else
                    return 0
                end
                """;

        String sha1 = script.scriptLoad(skuStocksDeduct);
        Integer re = script.evalSha(
                RScript.Mode.READ_WRITE,
                sha1,
                RScript.ReturnType.INTEGER,
                Collections.singletonList(RedisConstant.getSkuKey(basketDto.getSkuId())),
                RedisConstant.STOCKS,
                basketDto.getStocks(),
                RedisConstant.VERSION
        );

        return new Result(re == 1, basketDto);
    }


    @Data
    @AllArgsConstructor
    private static class Result {
        private boolean success;
        private BasketDto basketDto;
    }

    @Override
    public List<String> createOrderAndUserAddrOrder(List<BasketDto> basketDtoList) {
        Map<String, List<BasketDto>> basketDtoMap = basketDtoList.stream().collect(Collectors.groupingBy(BasketDto::getShopId));
        List<String> idList = new ArrayList<>();
        for (Map.Entry<String, List<BasketDto>> entry : basketDtoMap.entrySet()) {
            String orderSerialNumber = IdUtil.getSnowflake().nextIdStr();
            idList.add(orderSerialNumber);

            for (BasketDto basketDto : entry.getValue()) {
                // 订单地址保存
                UserAddrDto userAddrDto = StrUtil.isEmpty(basketDto.getAddrId()) ? userAddrService.getCommonAddr("userId") :
                        userAddrService.getUserAddrDtoByUserIdAndAddrId("userId", basketDto.getAddrId());
                UserAddrOrderDo userAddrOrderDo = BeanUtil.copyProperties(userAddrDto, UserAddrOrderDo.class);
                userAddrOrderDo.setOrderSerialNumber(orderSerialNumber);
                basketDto.setAddrOrderId(String.valueOf(userAddrOrderService.getBaseMapper().insert(userAddrOrderDo)));

                // 生成订单
                basketDto.setUserId("userId");
                this.createOrderByCart(basketDto);
            }
        }
        return idList;
    }

    @Override
    public String createOrderAndUserAddrOrder(OrderParam orderParam) {
        String orderSerialNumber = IdUtil.getSnowflake().nextIdStr();

        // 订单地址保存
        UserAddrDto userAddrDto = StrUtil.isEmpty(orderParam.getAddrId()) ? userAddrService.getCommonAddr("userId") :
                userAddrService.getUserAddrDtoByUserIdAndAddrId("userId", orderParam.getAddrId());
        UserAddrOrderDo userAddrOrderDo = BeanUtil.copyProperties(userAddrDto, UserAddrOrderDo.class);
        userAddrOrderDo.setOrderSerialNumber(orderSerialNumber);
        orderParam.setUserAddrOrderId(String.valueOf(userAddrOrderService.getBaseMapper().insert(userAddrOrderDo)));

        // 生成订单
        OrderDto orderDto = OrderConvert.INSTANCE.OrderParamToDto(orderParam);
        OrderDo orderDo = OrderConvert.INSTANCE.OrderDtoToDo(orderDto);
        orderDo.setUserId("userId");
        orderDo.setCreateTime(LocalDateTime.now());
        orderDo.setUpdateTime(LocalDateTime.now());
        this.save(orderDo);

        return orderSerialNumber;
    }

    @Override
    public void createOrderByCart(BasketDto basketDto) {
        OrderDo orderDo = BeanUtil.copyProperties(basketDto, OrderDo.class);
        orderDo.setCreateTime(LocalDateTime.now());
        orderDo.setUpdateTime(LocalDateTime.now());
        this.save(orderDo);
    }

    @Override
    public void rollbackOrder(List<String> orderSerialNumberList) {
        // This line calls the baseMapper to remove the order from the database
        this.baseMapper.removeBatchByOrderSerialNumberList(orderSerialNumberList);
    }

    @Override
    public void batchUpdateStatus(List<String> orderIdList) {
        this.baseMapper.batchUpdateStatus(orderIdList);
    }

    @Override
    public List<OrderDto> getTimeoutOrders() {
        List<OrderDo> timeoutOrders = this.baseMapper.getTimeoutOrders();
        return timeoutOrders.stream().map(OrderConvert.INSTANCE::OrderDoToDto).toList();
    }

}
