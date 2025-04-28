package com.xi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商品表
 * </p>
 *
 * @author 郑西
 * @since 2025-04-24
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("tb_prod")
public class Prod implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 产品ID
     */
    @TableId(value = "prod_id", type = IdType.AUTO)
    private String prodId;

    /**
     * 商品名称
     */
    private String prodName;

    /**
     * 店铺ID
     */
    private String shopId;

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
    private String categoryId;

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
