package com.xi.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品信息
 */
@Data
public class OrderItemDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订购流水号
     */
    private String orderSerialNumber;

    /**
     * 商品ID
     */
    private String prodId;

    /**
     * SkuID
     */
    private String skuId;

    /**
     * Sku价格
     */
    private BigDecimal price;

    /**
     * 商品数量
     */
    private Integer totalStocks;

    /**
     * Sku数量
     */
    private Integer stocks;

    /**
     * 商品备注
     */
    private String remarks;

    /**
     * 地址信息
     */
    private String addrId;

    /**
     * 商品总值
     */
    private BigDecimal cost;

    /**
     * 商品实际值
     */
    private BigDecimal actualCost;

}
