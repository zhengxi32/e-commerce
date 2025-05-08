package com.xi.entity.tb;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * 商品规格表
 * </p>
 *
 * @author 郑西
 * @since 2025-04-27
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("tb_sku")
public class SkuDo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 单品ID
     */
    @TableId(value = "sku_id", type = IdType.AUTO)
    private String skuId;

    /**
     * sku名称
     */
    private String skuName;

    /**
     * 商品ID
     */
    private String prodId;

    /**
     * 商品名称
     */
    private String prodName;

    /**
     * 销售属性组合字符串 格式是p1：v1;p2：v2
     */
    private String properties;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 实际库存
     */
    private Integer stocks;

    /**
     * 更新后库存
     */
    @TableField(exist = false)
    private Integer afterStocks;

    /**
     * 商家编码
     */
    private String partyCode;

    /**
     * 商品条形码
     */
    private String modelId;

    /**
     * sku图片
     */
    private String pic;

    /**
     * 商品重量
     */
    private Object weight;

    /**
     * 商品体积
     */
    private Object volume;

    /**
     * 0 禁用 1 启用
     */
    private Boolean status;

    /**
     * 录入时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 更新后版本号
     */
    @TableField(exist = false)
    private Integer afterVersion;
}
