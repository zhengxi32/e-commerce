package com.xi.strategy.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.google.common.collect.Lists;
import com.xi.constant.RedisConstant;
import com.xi.entity.dto.BasketDto;
import com.xi.entity.dto.SkuDto;
import com.xi.entity.param.OrderParam;
import com.xi.enums.ResponseCodeEnum;
import com.xi.exception.BizException;
import com.xi.service.BasketService;
import com.xi.service.OrderService;
import com.xi.service.SkuService;
import com.xi.strategy.StockDecreaseStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BasketPurchaseStrategy implements StockDecreaseStrategy {

    @Resource
    private SkuService skuService;

    @Resource
    private BasketService basketService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private OrderService orderService;

    @Override
    public void createOrder(OrderParam orderParam) {
        List<BasketDto> basketDtoList = orderParam.getBasketDtoList();

        // Redis库存预检查
        for (BasketDto basketDto : basketDtoList) {
            if (skuService.getSkuDtoBySkuId(basketDto.getSkuId()).getStocks() < basketDto.getStocks()) {
                throw new BizException(ResponseCodeEnum.STOCKS_NOT_ENOUGH);
            }
        }
        List<String> orderSerialNumberList = Lists.newArrayList();

        // 订单流水号
        for (BasketDto basketDto : basketDtoList) {
            String orderSerialNumber = IdUtil.getSnowflake().nextIdStr();
            basketDto.setOrderSerialNumber(orderSerialNumber);
            orderSerialNumberList.add(orderSerialNumber);
        }

        orderService.createOrderAndUserAddrOrder(basketDtoList);
        orderParam.setOrderSerialNumberList(orderSerialNumberList);
    }

    public boolean decreaseStock(OrderParam orderParam) {
        List<BasketDto> basketDtoList = orderParam.getBasketDtoList();

        // 分离热点商品
        List<BasketDto> hotItems = basketDtoList.stream()
                .filter(item -> isHotProduct(item.getSkuId()))
                .collect(Collectors.toList());

        // 对热点商品加锁
        if (!hotItems.isEmpty() && !lockHotItems(hotItems)) {
            return false;
        }

        try {
            return basketDtoList.stream().allMatch(item -> {
                SkuDto skuDto = skuService.getStocksAndVersionBySkuId(item.getSkuId());
                return skuService.updateStocksLock(item, skuDto.getVersion());
            });
        } finally {
            // 释放热点商品锁
            if (CollUtil.isNotEmpty(hotItems)) {
                RLock[] locks = hotItems.stream()
                        .map(item -> redissonClient.getLock(RedisConstant.SKU_LOCK+ item.getSkuId()))
                        .toArray(RLock[]::new);

                RedissonMultiLock multiLock = new RedissonMultiLock(locks);
                if (multiLock.isLocked() && multiLock.isHeldByCurrentThread()) {
                    multiLock.unlock();
                }
            }
        }
    }

    /**
     * 锁定热点商品
     */
    private boolean lockHotItems(List<BasketDto> hotItems) {
        // 按skuId排序避免死锁
        basketService.sortListBySkuIdAsc(hotItems);

        RLock[] locks = hotItems.stream()
                .map(item -> redissonClient.getLock(RedisConstant.SKU_LOCK + item.getSkuId()))
                .toArray(RLock[]::new);

        RedissonMultiLock multiLock = new RedissonMultiLock(locks);
        try {
            return multiLock.tryLock(50, 3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 判断是否为热点商品
     */
    private boolean isHotProduct(String skuId) {
        RScoredSortedSet<String> hotProducts = redissonClient.getScoredSortedSet(RedisConstant.HOT_PROD_KEY_SET);
        return hotProducts.contains(skuId);
    }
}