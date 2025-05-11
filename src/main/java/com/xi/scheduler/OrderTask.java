package com.xi.scheduler;

import com.xi.constant.RedisConstant;
import com.xi.constant.TopicConstant;
import com.xi.entity.dto.OrderDto;
import com.xi.service.OrderService;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RSet;
import org.redisson.api.RSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Resource
    private OrderService orderService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private RedissonClient redissonClient;

    @XxlJob("expiredOrderFailure")
    public void expiredOrderFailure() {
        log.info("The expired order becomes invalid and the scheduled task begins to be executed {}", LocalDateTime.now());
        List<OrderDto> timeoutOrders = orderService.getTimeoutOrders();

        rocketMQTemplate.asyncSend(TopicConstant.ORDER_CANCEL_TOPIC, timeoutOrders, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("The invalid message of the expired order was sent successfully {}", LocalDateTime.now());
            }

            @Override
            public void onException(Throwable e) {
                log.error("The invalid message of the expired order failed to be sent", e);
            }
        });
    }

    @XxlJob("HotspotDeletionAndReInsertion")
    public void HotspotDeletionAndReInsertion() {
        log.info("Hotspot deletion and reinsertion task begins to be executed {}", LocalDateTime.now());

        RSortedSet<Object> sortedSet = redissonClient.getSortedSet(RedisConstant.HOT_PROD_KEY_SET);
        RSet<Object> set = redissonClient.getSet(RedisConstant.VALID_PROD_KEY_SET);
        sortedSet.forEach(x -> {
            if (!set.contains(x)) {
                sortedSet.remove(x);
            }
        });
        int size = sortedSet.size();

        set.forEach(x -> {
            if (!sortedSet.contains(x) && sortedSet.size() < RedisConstant.HOT_PROD_KEY_SET_LIMIT) {
                sortedSet.add(x);
            }
            else Thread.currentThread().interrupt();
        });
    }
}
