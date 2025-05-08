package com.xi.strategy.Impl;

import com.xi.entity.dto.SkuDto;
import com.xi.entity.param.OrderParam;
import com.xi.enums.ResponseCodeEnum;
import com.xi.exception.BizException;
import com.xi.service.SkuService;
import com.xi.strategy.StockDecreaseStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DirectPurchaseStrategy implements StockDecreaseStrategy {

    @Resource
    private SkuService skuService;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public boolean decreaseStock(OrderParam orderParam) {
        RLock rLock = redissonClient.getLock(orderParam.getSkuId());
        try {
            // 尝试上锁
            rLock.tryLock(50, 3000, TimeUnit.MILLISECONDS);

            // 乐观锁库存扣减
            SkuDto skuDto = skuService.getStocksAndVersionBySkuId(orderParam.getSkuId());
            return skuService.updateStocksLock(orderParam, skuDto.getVersion());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BizException(ResponseCodeEnum.SYSTEM_ERROR);
        } finally {
            // 释放锁
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }
}