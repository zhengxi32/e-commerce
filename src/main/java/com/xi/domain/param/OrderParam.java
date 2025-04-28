package com.xi.domain.param;

import lombok.Data;

import java.util.List;

/**
 * 生成订单参数
 */
@Data
public class OrderParam {

    /**
     * 商品ID
     */
    private String prodId;

    /**
     * SkuID
     */
    private String skuId;

    /**
     * 店铺ID
     */
    private String shopId;

    /**
     * 地址ID
     */
    private String addrId;

    /**
     * 订单流水号
     */
    private String orderSerialId;

    /**
     * 商品数量
     */
    private Integer prodCount;

    /**
     * 购物车ID列表
     */
    private List<String> basketIdList;

    /**
     * 地址ID列表
     */
    private List<String> addrIdList;

}
