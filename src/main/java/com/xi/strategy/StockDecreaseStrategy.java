package com.xi.strategy;

import com.xi.annotation.StockDecreaseResult;
import com.xi.entity.param.OrderParam;

public interface StockDecreaseStrategy {

    /**
     * 创建订单策略
     * @param orderParam 订单参数
     */
    void createOrder(OrderParam orderParam);

    /**
     * 库存扣减策略
     * @param orderParam 订单参数
     * @return 扣减结果 @StockDecreaseResult根据结果发送消息到下游
     */
    @StockDecreaseResult
    boolean decreaseStock(OrderParam orderParam);

}
