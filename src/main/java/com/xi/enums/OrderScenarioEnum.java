package com.xi.enums;

import com.xi.constant.OrderTagConstant;
import com.xi.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderScenarioEnum {
    DIRECT_PURCHASE(OrderTagConstant.ORDER_TAG_DIRECT_PURCHASE, ""),
    BASKET_PURCHASE(OrderTagConstant.ORDER_TAG_BASKET_PURCHASE, ""),
    SEC_KILL_DIRECT_PURCHASE(OrderTagConstant.ORDER_TAG_SEC_KILL_DIRECT_PURCHASE, ""),
    SEC_KILL_BASKET_PURCHASE(OrderTagConstant.ORDER_TAG_SEC_KILL_BASKET_PURCHASE, ""),
    ;


    private final String tag;

    private final String strategyBeanName;



    public static OrderScenarioEnum getByTag(String tag) {
        for (OrderScenarioEnum orderType : OrderScenarioEnum.values()) {
            if (orderType.getTag().equals(tag)) {
                return orderType;
            }
        }
        throw new BizException(ResponseCodeEnum.ORDER_SCENARIO_NOT_EXIST);
    }
}
