package com.xi.entity.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserAddrOrderDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
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
     * 订购流水号
     */
    private String orderSerialNumber;

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
