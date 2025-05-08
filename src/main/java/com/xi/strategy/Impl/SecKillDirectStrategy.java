package com.xi.strategy.Impl;

import com.xi.constant.TopicConstant;
import com.xi.entity.dto.SkuDto;
import com.xi.entity.param.OrderParam;
import com.xi.service.SkuService;
import com.xi.strategy.StockDecreaseStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SecKillDirectStrategy implements StockDecreaseStrategy {

    @Resource
    private SkuService skuService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void decreaseStock(OrderParam orderParam) {
        // 乐观锁库存扣减
        SkuDto skuDto = skuService.getStocksAndVersionBySkuId(orderParam.getSkuId());
        Boolean success = skuService.updateStocksLock(orderParam.getSkuId(), orderParam.getProdCount(), skuDto.getVersion());

        // 库存扣减结果发送至不同主题
        String nextTopic = success ? TopicConstant.PAYMENT_TOPIC : TopicConstant.ORDER_ROLLBACK_TOPIC;
        rocketMQTemplate.syncSend(nextTopic, orderParam);
    }
}
