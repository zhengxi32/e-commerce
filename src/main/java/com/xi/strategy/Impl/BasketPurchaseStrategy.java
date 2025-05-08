package com.xi.strategy.Impl;

import com.xi.constant.SystemConstant;
import com.xi.constant.TopicConstant;
import com.xi.entity.dto.BasketDto;
import com.xi.entity.dto.SkuDto;
import com.xi.entity.param.OrderParam;
import com.xi.enums.ResponseCodeEnum;
import com.xi.exception.BizException;
import com.xi.service.BasketService;
import com.xi.service.SkuService;
import com.xi.strategy.StockDecreaseStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
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
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void decreaseStock(OrderParam orderParam) {
        List<BasketDto> basketDtoList = orderParam.getBasketDtoList();

        // Redisson分布式锁
        RLock[] rLocks = new RLock[basketDtoList.size()];

        // SkuID排序 避免死锁
        basketService.sortListBySkuIdAsc(basketDtoList);

        for (BasketDto basketDto : basketDtoList) {
            rLocks[0] = redissonClient.getLock(SystemConstant.LOCK + basketDto.getSkuId());
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
                    Boolean success = skuService.updateStocksLock(basketDto.getSkuId(), basketDto.getStocks(), skuDto.getVersion());
                    flag &= success;
                }

                redissonMultiLock.unlock();

                // 根据结果判断是否回滚与主题发送
                if (!flag) {
                    rocketMQTemplate.syncSend(TopicConstant.ORDER_ROLLBACK_TOPIC, orderParam);
                    throw new BizException(ResponseCodeEnum.STOCKS_NOT_ENOUGH);
                }
                rocketMQTemplate.syncSend(TopicConstant.PAYMENT_TOPIC, orderParam);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // 释放锁
            redissonMultiLock.unlock();
        }
    }
}
