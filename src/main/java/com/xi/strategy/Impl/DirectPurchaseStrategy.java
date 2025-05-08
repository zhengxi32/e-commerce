package com.xi.strategy.Impl;

import com.xi.constant.TopicConstant;
import com.xi.entity.dto.SkuDto;
import com.xi.entity.param.OrderParam;
import com.xi.enums.ResponseCodeEnum;
import com.xi.exception.BizException;
import com.xi.service.SkuService;
import com.xi.strategy.StockDecreaseStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
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
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private RedissonClient redissonClient;


    @Override
    public void decreaseStock(OrderParam orderParam) {
        RLock rLock = redissonClient.getLock(orderParam.getSkuId());
        try {
            // 尝试上锁
            rLock.tryLock(50, 3000, TimeUnit.MILLISECONDS);

            // 乐观锁库存扣减
            SkuDto skuDto = skuService.getStocksAndVersionBySkuId(orderParam.getSkuId());
            Boolean success = skuService.updateStocksLock(orderParam.getSkuId(), orderParam.getProdCount(), skuDto.getVersion());

            rLock.unlock();

            // 库存扣减结果发送至不同主题
            String nextTopic = success ? TopicConstant.PAYMENT_TOPIC : TopicConstant.ORDER_ROLLBACK_TOPIC;
            rocketMQTemplate.syncSend(nextTopic, orderParam);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            // 进入死信队列 todo
            throw new BizException(ResponseCodeEnum.SYSTEM_ERROR);
        } finally {
            // 释放锁
            rLock.unlock();
        }
    }
}
