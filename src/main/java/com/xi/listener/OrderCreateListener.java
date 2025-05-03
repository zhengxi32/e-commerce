package com.xi.listener;

import com.xi.domain.param.OrderParam;
import com.xi.mapper.OrderMapper;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RocketMQTransactionListener
public class OrderCreateListener implements RocketMQLocalTransactionListener {

    @Resource
    private OrderMapper orderMapper;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object args) {
        return getRocketMQLocalTransactionState(message);
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        return getRocketMQLocalTransactionState(message);
    }

    private RocketMQLocalTransactionState getRocketMQLocalTransactionState(Message message) {
        OrderParam orderParam = (OrderParam) message.getPayload();
        List<String> orderSerialNumberList = orderParam.getOrderSerialNumberList();

        boolean flag = true;
        for (String orderSerialNumber : orderSerialNumberList) {
            flag &= orderMapper.existsById(orderSerialNumber);
        }

        return flag ? RocketMQLocalTransactionState.COMMIT : RocketMQLocalTransactionState.ROLLBACK;
    }
}
