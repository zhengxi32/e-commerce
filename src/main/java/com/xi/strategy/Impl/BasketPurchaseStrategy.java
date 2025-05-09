package com.xi.strategy.Impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.google.common.collect.Lists;
import com.xi.constant.OrderTagConstant;
import com.xi.constant.SystemConstant;
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
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Override
    public boolean decreaseStock(OrderParam orderParam) {
        List<BasketDto> basketDtoList = orderParam.getBasketDtoList();

        // Redisson分布式锁
        RLock[] rLocks = new RLock[basketDtoList.size()];

        // SkuID排序 避免死锁
        basketService.sortListBySkuIdAsc(basketDtoList);

        for (int i = 0; i < basketDtoList.size(); i++) {
            rLocks[i] = redissonClient.getLock(SystemConstant.LOCK + basketDtoList.get(i).getSkuId());
        }
        RedissonMultiLock redissonMultiLock = new RedissonMultiLock(rLocks);

        try {
            // 设置锁的等待时间和超时时间
            boolean acquired = redissonMultiLock.tryLock(50, 3000, TimeUnit.MILLISECONDS);
            boolean flag = true;

            // 获取到锁 业务执行
            if (acquired) {
                for (BasketDto basketDto : basketDtoList) {
                    // 乐观锁库存扣减
                    SkuDto skuDto = skuService.getStocksAndVersionBySkuId(basketDto.getSkuId());
                    Boolean success = skuService.updateStocksLock(basketDto, skuDto.getVersion());
                    flag &= success;
                }
                return flag;
            }
            throw new BizException(ResponseCodeEnum.STOCKS_NOT_ENOUGH);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BizException(ResponseCodeEnum.SYSTEM_ERROR);
        } finally {
            // 释放锁
            if (redissonMultiLock.isLocked() && redissonMultiLock.isHeldByCurrentThread()) {
                redissonMultiLock.unlock();
            }
        }
    }
}