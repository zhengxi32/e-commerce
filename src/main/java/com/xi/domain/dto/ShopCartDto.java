package com.xi.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 店铺维度商品信息
 */
@Data
public class ShopCartDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 店铺ID
     */
    private String shopId;

    /**
     * 店铺名字
     */
    private String shopName;

    /**
     * 商品信息 直接下单使用
     */
    private OrderItemDto orderItemDto;

    /**
     * 商品列表
     */
    private List<OrderItemDto> orderItemDtoList;



}
