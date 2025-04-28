package com.xi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 购物车
 * </p>
 *
 * @author 郑西
 * @since 2025-04-27
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("tb_basket")
public class Basket implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "basket_id", type = IdType.AUTO)
    private String basketId;

    /**
     * 店铺ID
     */
    private String shopId;

    /**
     * 产品ID
     */
    private String prodId;

    /**
     * SkuID
     */
    private String skuId;

    /**
     * 用户ID
     */
    private String userId;
}
