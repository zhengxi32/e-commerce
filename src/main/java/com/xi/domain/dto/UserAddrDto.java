package com.xi.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserAddrDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "addr_id", type = IdType.AUTO)
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
     * 邮编
     */
    private String postCode;

    /**
     * 地址
     */
    private String addr;

    /**
     * 手机
     */
    private String mobile;

    /**
     * 状态, 1正常, 0无效
     */
    private String status;

    /**
     * 是否默认地址 1是
     */
    private Integer commonAddr;

}
