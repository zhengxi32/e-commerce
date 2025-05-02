package com.xi.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BasketDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String basketId;

    /**
     * 店铺ID
     */
    private String shopId;

    /**
     * 店铺名字
     */
    private String shopName;

    /**
     * 产品ID
     */
    private String prodId;

    /**
     * 产品名字
     */
    private String prodName;

    /**
     * SkuID
     */
    private String skuId;

    /**
     * Sku名
     */
    private String skuName;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 地址ID
     */
    private String addrId;

    /**
     * 订单地址ID
     */
    private String addrOrderId;

    /**
     * 商品信息
     */
    private ProdDto prodDto;

    /**
     * Sku信息
     */
    private SkuDto skuDto;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 状态, 1正常, 0无效
     */
    private String status;

    /**
     * 加入时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 商品数量
     */
    private Integer totalStocks;

    /**
     * Sku数量
     */
    private Integer stocks;

    /**
     * 订购流水号
     */
    private String orderSerialNumber;

    /**
     * 处理标识位
     */
    private volatile Boolean deal;

}
