package com.xi.strategy.Impl;

import com.xi.constant.TopicConstant;
import com.xi.entity.dto.BasketDto;
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
public class SecKillBasketStrategy implements StockDecreaseStrategy {

    @Resource
    private SkuService skuService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void decreaseStock(OrderParam orderParam) {
        boolean flag = true;

        for (BasketDto basketDto : orderParam.getBasketDtoList()) {
            // 乐观锁库存扣减
            SkuDto skuDto = skuService.getStocksAndVersionBySkuId(basketDto.getSkuId());
            Boolean success = skuService.updateStocksLock(basketDto.getSkuId(), basketDto.getStocks(), skuDto.getVersion());
            flag &= success;
        }

        // 库存扣减结果发送至不同主题
        String nextTopic = flag ? TopicConstant.PAYMENT_TOPIC : TopicConstant.ORDER_ROLLBACK_TOPIC;
        rocketMQTemplate.syncSend(nextTopic, orderParam);
    }
}
