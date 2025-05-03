package com.xi.listener;

import com.xi.constant.ConsumerConstant;
import com.xi.constant.TagConstant;
import com.xi.constant.TopicConstant;
import com.xi.domain.param.OrderParam;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(
        topic = TopicConstant.PAYMENT_TOPIC,
        consumerGroup = ConsumerConstant.PAYMENT_GROUP,
        selectorExpression = TagConstant.ORDER_TAG_DIRECT_PURCHASE + TagConstant.SEPARATOR + TagConstant.ORDER_TAG_BASKET_PURCHASE +
                TagConstant.SEPARATOR + TagConstant.ORDER_TAG_SEC_KILL_DIRECT_PURCHASE + TagConstant.SEPARATOR + TagConstant.ORDER_TAG_SEC_KILL_BASKET_PURCHASE
)
public class PaymentListener implements RocketMQListener<OrderParam> {

    @Override
    public void onMessage(OrderParam orderParam) {

    }
}
