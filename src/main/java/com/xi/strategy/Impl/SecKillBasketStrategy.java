package com.xi.strategy.Impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.google.common.collect.Lists;
import com.xi.constant.OrderTagConstant;
import com.xi.constant.RedisConstant;
import com.xi.entity.dto.BasketDto;
import com.xi.entity.dto.SkuDto;
import com.xi.entity.param.OrderParam;
import com.xi.enums.ResponseCodeEnum;
import com.xi.exception.BizException;
import com.xi.service.Impl.OrderServiceImpl;
import com.xi.service.OrderService;
import com.xi.service.SkuService;
import com.xi.strategy.StockDecreaseStrategy;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
public class SecKillBasketStrategy implements StockDecreaseStrategy {

    @Resource
    private SkuService skuService;

    @Resource
    private OrderService orderService;

    @Resource
    private RedissonClient redissonClient;

    @Resource(name = "secKillStockDecreaseThreadPool")
    private ExecutorService executorService;

    @Override
    public void createOrder(OrderParam orderParam) {
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

        List<String> orderSerialNumberList = Lists.newArrayList();

        // 订单流水号
        for (BasketDto basketDto : basketDtoList) {
            String orderSerialNumber = IdUtil.getSnowflake().nextIdStr();
            basketDto.setOrderSerialNumber(orderSerialNumber);
            orderSerialNumberList.add(orderSerialNumber);
        }

        // 创建订单
        orderService.createOrderAndUserAddrOrder(orderParam.getBasketDtoList());
        orderParam.setOrderSerialNumberList(orderSerialNumberList);
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
    public boolean decreaseStock(OrderParam orderParam) {
        boolean flag = true;

        for (BasketDto basketDto : orderParam.getBasketDtoList()) {
            // 乐观锁库存扣减
            SkuDto skuDto = skuService.getStocksAndVersionBySkuId(basketDto.getSkuId());
            Boolean success = skuService.updateStocksLock(basketDto, skuDto.getVersion());
            flag &= success;
        }

        return flag;
    }
}