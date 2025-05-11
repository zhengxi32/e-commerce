package com.xi.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.google.common.collect.Lists;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Preconditions;
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
import com.xi.enums.OrderScenarioEnum;
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
import org.springframework.messaging.Message;
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
    private UserAddrService userAddrService;

    @Resource
    private UserAddrOrderService userAddrOrderService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void submitOrder(OrderParam orderParam) {
        // 订单参数校验
        checkValid(orderParam);
        // 发送半事务消息
        rocketMQTemplate.sendMessageInTransaction(
                TopicConstant.ORDER_CREATE_TOPIC,
                MessageBuilder.withPayload(orderParam).setHeader(MessageConst.PROPERTY_TAGS, OrderTagConstant.ORDER_TAG_DIRECT_PURCHASE).build(),
                null
        );
    }

    /**
     * 根据策略类型校验参数合法性
     * @param orderParam 订单参数
     */
    private void checkValid(OrderParam orderParam) {
        if (orderParam.getTag().equals(OrderTagConstant.ORDER_TAG_BASKET_PURCHASE) || orderParam.getTag().equals(OrderTagConstant.ORDER_TAG_SEC_KILL_BASKET_PURCHASE)) {
            if (CollUtil.isEmpty(orderParam.getBasketDtoList())) {
                throw new BizException(ResponseCodeEnum.ORDER_PARAM_ERROR);
            }
            for (BasketDto basketDto : orderParam.getBasketDtoList()) {
                if (StrUtil.isEmpty(basketDto.getSkuId()) || ObjUtil.isEmpty(basketDto.getStocks())) {
                    throw new BizException(ResponseCodeEnum.ORDER_PARAM_ERROR);
                }
            }
        }
        if (orderParam.getTag().equals(OrderTagConstant.ORDER_TAG_DIRECT_PURCHASE) || orderParam.getTag().equals(OrderTagConstant.ORDER_TAG_SEC_KILL_DIRECT_PURCHASE)) {
            if (StrUtil.isEmpty(orderParam.getSkuId()) || ObjUtil.isEmpty(orderParam.getStocks())) {
                throw new BizException(ResponseCodeEnum.ORDER_PARAM_ERROR);
            }
        }
    }

    @Override
    public void createOrderAndUserAddrOrder(List<BasketDto> basketDtoList) {
        Map<String, List<BasketDto>> basketDtoMap = basketDtoList.stream().collect(Collectors.groupingBy(BasketDto::getShopId));
        List<String> idList = new ArrayList<>();
        for (Map.Entry<String, List<BasketDto>> entry : basketDtoMap.entrySet()) {

            for (BasketDto basketDto : entry.getValue()) {
                // 订单地址保存
                UserAddrDto userAddrDto = StrUtil.isEmpty(basketDto.getAddrId()) ? userAddrService.getCommonAddr("userId") :
                        userAddrService.getUserAddrDtoByUserIdAndAddrId("userId", basketDto.getAddrId());
                UserAddrOrderDo userAddrOrderDo = BeanUtil.copyProperties(userAddrDto, UserAddrOrderDo.class);
                userAddrOrderDo.setOrderSerialNumber(basketDto.getOrderSerialNumber());
                basketDto.setAddrOrderId(String.valueOf(userAddrOrderService.getBaseMapper().insert(userAddrOrderDo)));

                // 生成订单
                basketDto.setUserId("userId");
                this.createOrderByCart(basketDto);
            }
        }
    }

    @Override
    public void createOrderAndUserAddrOrder(OrderParam orderParam) {
        if (CollUtil.isEmpty(orderParam.getOrderSerialNumberList())) {
            throw new BizException(ResponseCodeEnum.ORDER_SERIAL_NUMBER_MISSING);
        }

        // 订单地址保存
        UserAddrDto userAddrDto = StrUtil.isEmpty(orderParam.getAddrId()) ? userAddrService.getCommonAddr("userId") :
                userAddrService.getUserAddrDtoByUserIdAndAddrId("userId", orderParam.getAddrId());
        UserAddrOrderDo userAddrOrderDo = BeanUtil.copyProperties(userAddrDto, UserAddrOrderDo.class);
        userAddrOrderDo.setOrderSerialNumber(orderParam.getOrderSerialNumberList().get(0));
        orderParam.setUserAddrOrderId(String.valueOf(userAddrOrderService.getBaseMapper().insert(userAddrOrderDo)));

        // 生成订单
        OrderDto orderDto = OrderConvert.INSTANCE.OrderParamToDto(orderParam);
        OrderDo orderDo = OrderConvert.INSTANCE.OrderDtoToDo(orderDto);
        orderDo.setUserId("userId");
        orderDo.setCreateTime(LocalDateTime.now());
        orderDo.setUpdateTime(LocalDateTime.now());
        this.save(orderDo);
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
