package com.xi.listener;

import com.xi.constant.ConsumerConstant;
import com.xi.constant.TopicConstant;
import com.xi.entity.dto.OrderDto;
import com.xi.service.OrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RocketMQMessageListener(
        topic = TopicConstant.ORDER_CANCEL_TOPIC,
        consumerGroup = ConsumerConstant.ORDER_CANCEL_GROUP
)
@Slf4j
public class OrderCancelListener implements RocketMQListener<List<OrderDto>> {

    @Resource
    private OrderService orderService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void onMessage(List<OrderDto> orderDtoList) {
        // 更新订单状态 幂等性
        List<String> orderIdList = orderDtoList.stream().map(OrderDto::getOrderId).toList();
        orderService.batchUpdateStatus(orderIdList);

        // 发送至库存回滚主题
        rocketMQTemplate.syncSend(TopicConstant.STOCK_RELEASE_TOPIC, orderDtoList);
    }
}
