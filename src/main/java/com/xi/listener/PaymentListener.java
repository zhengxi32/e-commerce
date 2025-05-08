package com.xi.listener;

import com.xi.constant.ConsumerConstant;
import com.xi.constant.OrderTagConstant;
import com.xi.constant.TopicConstant;
import com.xi.entity.param.OrderParam;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(
        topic = TopicConstant.PAYMENT_TOPIC,
        consumerGroup = ConsumerConstant.PAYMENT_GROUP,
        selectorExpression = OrderTagConstant.ORDER_TAG_DIRECT_PURCHASE + OrderTagConstant.SEPARATOR + OrderTagConstant.ORDER_TAG_BASKET_PURCHASE +
                OrderTagConstant.SEPARATOR + OrderTagConstant.ORDER_TAG_SEC_KILL_DIRECT_PURCHASE + OrderTagConstant.SEPARATOR + OrderTagConstant.ORDER_TAG_SEC_KILL_BASKET_PURCHASE
)
public class PaymentListener implements RocketMQListener<OrderParam> {

    @Override
    public void onMessage(OrderParam orderParam) {

    }
}
