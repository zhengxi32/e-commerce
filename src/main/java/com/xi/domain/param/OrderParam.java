package com.xi.domain.param;

import lombok.Data;

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
     * 商品数量
     */
    private Integer prodCount;


}
