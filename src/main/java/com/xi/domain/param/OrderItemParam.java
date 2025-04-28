package com.xi.domain.param;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 订单项参数
 */
@Data
public class OrderItemParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    private String prodId;

    /**
     * 商品备注
     */
    private String remarks;

}
