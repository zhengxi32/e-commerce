package com.xi.listener;

import com.xi.constant.ConsumerConstant;
import com.xi.constant.TopicConstant;
import com.xi.entity.dto.OrderDto;
import com.xi.service.SkuService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RocketMQMessageListener(
        topic = TopicConstant.STOCK_RELEASE_TOPIC,
        consumerGroup = ConsumerConstant.STOCK_RELEASE_GROUP
)
@Slf4j
public class StockReleaseListener implements RocketMQListener<List<OrderDto>> {

    @Resource
    private SkuService skuService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void onMessage(List<OrderDto> orderDtoList) {
        log.info("Stocks release begins {}", LocalDateTime.now());
        Map<String, Integer> skuIdMap = new HashMap<>();
        orderDtoList.forEach(orderDto -> {
            skuIdMap.merge(orderDto.getSkuId(), orderDto.getProdCount(), Integer::sum);
        });
        for (Map.Entry<String, Integer> entry : skuIdMap.entrySet()) {
            skuService.releaseStock(entry.getKey(), entry.getValue());
        }
        log.info("The stocks release was successfully executed {}", LocalDateTime.now());
        rocketMQTemplate.syncSend(TopicConstant.SKU_CACHE_STOCK_SYNC, orderDtoList);
    }
}
