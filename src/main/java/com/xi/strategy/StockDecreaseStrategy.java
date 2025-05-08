package com.xi.strategy;

import com.xi.annotation.StockDecreaseResult;
import com.xi.entity.param.OrderParam;

public interface StockDecreaseStrategy {
    @StockDecreaseResult
    boolean decreaseStock(OrderParam orderParam);

}
