package com.xi.listener;

import cn.hutool.db.sql.Order;
import com.xi.entity.param.OrderParam;
import com.xi.factory.OrderStrategyFactory;
import com.xi.mapper.OrderMapper;
import com.xi.strategy.StockDecreaseStrategy;
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

    @Resource
    private OrderStrategyFactory orderStrategyFactory;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object args) {
        OrderParam orderParam = (OrderParam) message.getPayload();
        // 根据订单类型获取对应策略
        StockDecreaseStrategy stockDecreaseStrategy = orderStrategyFactory.getStrategy(orderParam.getTag());
        // 订单创建
        stockDecreaseStrategy.createOrder(orderParam);
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
