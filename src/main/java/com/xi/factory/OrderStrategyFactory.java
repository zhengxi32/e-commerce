package com.xi.factory;

import com.xi.enums.OrderScenarioEnum;
import com.xi.strategy.StockDecreaseStrategy;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OrderStrategyFactory {

    @Resource
    private Map<String, StockDecreaseStrategy> stockDecreaseStrategyMap;

    public StockDecreaseStrategy getStrategy(String tag) {
        OrderScenarioEnum orderScenarioEnum = OrderScenarioEnum.getByTag(tag);
        return stockDecreaseStrategyMap.get(orderScenarioEnum.getStrategyBeanName());
    }

}
