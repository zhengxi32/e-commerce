package com.xi.listener;

import com.xi.constant.ConsumerConstant;
import com.xi.constant.TopicConstant;
import com.xi.domain.dto.OrderDto;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;

import java.util.List;

@RocketMQMessageListener(
        topic = TopicConstant.CACHE_STOCK_SYNC,
        consumerGroup = ConsumerConstant.CACHE_STOCK_SYNC_GROUP
)
public class CacheStockSyncListener implements RocketMQListener<List<OrderDto>> {
    @Override
    public void onMessage(List<OrderDto> orderDtoList) {

    }
}
