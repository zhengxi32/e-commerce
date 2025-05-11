package com.xi.strategy.Impl;

import cn.hutool.core.util.IdUtil;
import com.xi.annotation.RedisLock;
import com.xi.constant.OrderTagConstant;
import com.xi.constant.RedisConstant;
import com.xi.entity.dto.SkuDto;
import com.xi.entity.param.OrderParam;
import com.xi.enums.ResponseCodeEnum;
import com.xi.exception.BizException;
import com.xi.service.OrderService;
import com.xi.service.SkuService;
import com.xi.strategy.StockDecreaseStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DirectPurchaseStrategy implements StockDecreaseStrategy {

    @Resource
    private SkuService skuService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private OrderService orderService;

    @Override
    public void createOrder(OrderParam orderParam) {
        // 订单流水
        orderParam.setOrderSerialNumberList(Collections.singletonList(IdUtil.getSnowflake().nextIdStr()));
        // 创建订单
        orderService.createOrderAndUserAddrOrder(orderParam);
    }

    @Override
    public boolean decreaseStock(OrderParam orderParam) {
        // 1. 检查是否为热点商品
        RScoredSortedSet<String> hotProducts = redissonClient.getScoredSortedSet(RedisConstant.HOT_PROD_KEY_SET);
        boolean isHotSpot = hotProducts.contains(orderParam.getProdId());

        // 2. 热点商品处理（加分布式锁）
        if (isHotSpot) {
            return decreaseStockWithDistributedLock(orderParam);
        }

        // 3. 非热点商品处理（仅乐观锁）
        return decreaseStockWithOptimisticLock(orderParam);
    }

    /**
     * 带分布式锁的库存扣减
     */
    private boolean decreaseStockWithDistributedLock(OrderParam orderParam) {
        RLock rLock = redissonClient.getLock(orderParam.getSkuId());
        try {
            // 尝试获取锁
            if (rLock.tryLock(50, 3000, TimeUnit.MILLISECONDS)) {
                // 乐观锁库存扣减
                SkuDto skuDto = skuService.getStocksAndVersionBySkuId(orderParam.getSkuId());
                return skuService.updateStocksLock(orderParam, skuDto.getVersion());
            }
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BizException(ResponseCodeEnum.SYSTEM_ERROR);
        } finally {
            // 确保只有持有锁的线程才能解锁
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

    /**
     * 仅使用乐观锁的库存扣减
     */
    private boolean decreaseStockWithOptimisticLock(OrderParam orderParam) {
        SkuDto skuDto = skuService.getStocksAndVersionBySkuId(orderParam.getSkuId());
        return skuService.updateStocksLock(orderParam, skuDto.getVersion());
    }
}