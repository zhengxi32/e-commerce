package com.xi.domain.message;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProdMessage {

    /**
     * 产品ID
     */
    private String prodId;

    /**
     * 商品名称
     */
    private String prodName;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 现价
     */
    private BigDecimal price;

    /**
     * 描述
     */
    private String brief;

    /**
     * 商品主图
     */
    private String pic;

    /**
     * 商品图片，以,分割
     */
    private String images;

    /**
     * 默认是1, 表示正常状态, 2表示下架, 3表示删除
     */
    private Boolean status;

    /**
     * 商品分类
     */
    private Long categoryId;

    /**
     * 销量
     */
    private Integer soldNum;

    /**
     * 总库存
     */
    private Integer totalStocks;

    /**
     * 录入时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 上架时间
     */
    private LocalDateTime putawayTime;

    /**
     * 版本号
     */
    private Integer version;

}
