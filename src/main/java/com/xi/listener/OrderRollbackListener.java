package com.xi.listener;

import com.xi.constant.ConsumerConstant;
import com.xi.constant.OrderTagConstant;
import com.xi.constant.TopicConstant;
import com.xi.entity.param.OrderParam;
import com.xi.service.OrderService;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RocketMQMessageListener(
        topic = TopicConstant.ORDER_ROLLBACK_TOPIC,
        consumerGroup = ConsumerConstant.ORDER_ROLLBACK_GROUP,
        selectorExpression = OrderTagConstant.ORDER_TAG_DIRECT_PURCHASE + OrderTagConstant.SEPARATOR + OrderTagConstant.ORDER_TAG_BASKET_PURCHASE +
                OrderTagConstant.SEPARATOR + OrderTagConstant.ORDER_TAG_SEC_KILL_DIRECT_PURCHASE + OrderTagConstant.SEPARATOR + OrderTagConstant.ORDER_TAG_SEC_KILL_BASKET_PURCHASE
)
public class OrderRollbackListener implements RocketMQListener<OrderParam> {

    @Resource
    private OrderService orderService;

    @Override
    public void onMessage(OrderParam orderParam) {
        // 获取已创建成功订单
        List<String> orderSerialNumberList = orderParam.getOrderSerialNumberList();
        // 回滚
        orderService.rollbackOrder(orderSerialNumberList);
    }

}
