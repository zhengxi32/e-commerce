package com.xi.strategy;

import com.xi.entity.param.OrderParam;

public interface StockDecreaseStrategy {

    void decreaseStock(OrderParam orderParam);

}
