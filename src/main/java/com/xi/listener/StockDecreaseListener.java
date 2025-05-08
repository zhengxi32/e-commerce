package com.xi.listener;

import com.xi.constant.ConsumerConstant;
import com.xi.constant.OrderTagConstant;
import com.xi.constant.TopicConstant;
import com.xi.entity.param.OrderParam;
import com.xi.factory.OrderStrategyFactory;
import com.xi.service.BasketService;
import com.xi.service.SkuService;
import com.xi.strategy.StockDecreaseStrategy;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(
        topic = TopicConstant.ORDER_CREATE_TOPIC,
        consumerGroup = ConsumerConstant.STOCK_DECREASE_GROUP,
        selectorExpression = OrderTagConstant.ORDER_TAG_DIRECT_PURCHASE + OrderTagConstant.SEPARATOR + OrderTagConstant.ORDER_TAG_BASKET_PURCHASE +
        OrderTagConstant.SEPARATOR + OrderTagConstant.ORDER_TAG_SEC_KILL_DIRECT_PURCHASE + OrderTagConstant.SEPARATOR + OrderTagConstant.ORDER_TAG_SEC_KILL_BASKET_PURCHASE
)
public class StockDecreaseListener implements RocketMQListener<OrderParam> {

    @Resource
    private SkuService skuService;

    @Resource
    private BasketService basketService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private OrderStrategyFactory orderStrategyFactory;

    @Override
    public void onMessage(OrderParam orderParam) {
        // 根据订单类型获取对应的库存扣减策略
        StockDecreaseStrategy stockDecreaseStrategy = orderStrategyFactory.getStrategy(orderParam.getTag());
        // 扣减库存
        stockDecreaseStrategy.decreaseStock(orderParam);
    }
}
