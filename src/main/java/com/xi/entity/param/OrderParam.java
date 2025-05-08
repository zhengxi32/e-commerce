package com.xi.entity.param;

import com.xi.entity.dto.BasketDto;
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
     * 订单地址ID
     */
    private String userAddrOrderId;

    /**
     * 订单流水号列表
     */
    private List<String> orderSerialNumberList;

    /**
     * 商品数量
     */
    private Integer prodCount;

    /**
     * 购物车列表
     */
    private List<BasketDto> basketDtoList;

    /**
     * Tag类型
     */
    private String tag;

    /**
     * 更新后版本号
     */
    private Integer afterVersion;

    /**
     * 更新后库存
     */
    private Integer afterStocks;

}
