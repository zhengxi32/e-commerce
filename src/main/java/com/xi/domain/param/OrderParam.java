package com.xi.domain.param;

import com.xi.domain.dto.BasketDto;
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
     * 商品名字
     */
    private String prodName;

    /**
     * SkuID
     */
    private String skuId;

    /**
     * Sku名字
     */
    private String skuName;

    /**
     * 店铺ID
     */
    private String shopId;

    /**
     * 店铺名字
     */
    private String shopName;

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
     * 购物车列表
     */
    private List<BasketDto> basketDtoList;

    /**
     * 地址ID列表
     */
    private List<String> addrIdList;

}
