package com.xi.domain;

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
 * 用户订单配送地址
 * </p>
 *
 * @author 郑西
 * @since 2025-04-27
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("tb_user_addr_order")
public class UserAddrOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "addr_order_id", type = IdType.AUTO)
    private String addrOrderId;

    /**
     * 地址ID
     */
    private String addrId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 收货人
     */
    private String receiver;

    /**
     * 省
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区
     */
    private String area;

    /**
     * 地址
     */
    private String addr;

    /**
     * 邮编
     */
    private String postCode;

    /**
     * 手机
     */
    private String mobile;

    /**
     * 建立时间
     */
    private LocalDateTime createTime;

    /**
     * 版本号
     */
    private Integer version;
}
