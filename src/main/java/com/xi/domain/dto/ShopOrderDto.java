package com.xi.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单商品信息
 */
@Data
public class ShopOrderDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单流水号
     */
    private String orderSerialId;

    /**
     * 总值
     */
    private BigDecimal value;

    /**
     * 实际总值
     */
    private BigDecimal actualValue;

    /**
     * 商品总数
     */
    private Integer totalCount;

    /**
     * 优惠金额
     */
    private BigDecimal orderReduce;

    /**
     * 单店铺商品列表 直接下单使用
     */
    private ShopCartDto shopCartDto;

    /**
     * 各店铺商品列表 购物车下单使用
     */
    private List<ShopCartDto> shopCartDtoList;

    /**
     * 默认地址信息
     */
    private UserAddrDto userAddrDto;

}
