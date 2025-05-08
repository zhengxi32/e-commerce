package com.xi.entity.tb;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

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
public class BasketDo implements Serializable {

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

}
