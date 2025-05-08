package com.xi.scheduler;

import com.xi.constant.TopicConstant;
import com.xi.entity.dto.OrderDto;
import com.xi.service.OrderService;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Resource
    private OrderService orderService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @XxlJob("expiredOrderFailure")
    public void expiredOrderFailure() {
        log.info("过期订单失效定时任务开始执行，执行时间: {}", LocalDateTime.now());
        List<OrderDto> timeoutOrders = orderService.getTimeoutOrders();

        rocketMQTemplate.asyncSend(TopicConstant.ORDER_CANCEL_TOPIC, timeoutOrders, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("过期订单失效消息发送成功，执行完毕时间: {}", LocalDateTime.now());
            }

            @Override
            public void onException(Throwable e) {
                log.error("过期订单失效消息发送失败", e);
            }
        });
    }

}
