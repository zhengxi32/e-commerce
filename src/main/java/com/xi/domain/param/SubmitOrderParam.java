package com.xi.domain.param;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 提交订单参数
 */
@Data
public class SubmitOrderParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单流水号
     */
    private String orderSerialId;

    /**
     * 订单项参数
     */
    private List<OrderItemParam> orderItemParamList;

}
