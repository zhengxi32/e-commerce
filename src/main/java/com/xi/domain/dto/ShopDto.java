package com.xi.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ShopDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 店铺ID
     */
    private String shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店长用户ID
     */
    private String userId;

    /**
     * 店铺类型
     */
    private Boolean shopType;

    /**
     * 店铺简介
     */
    private String intro;

    /**
     * 店铺公告
     */
    private String shopNotice;

    /**
     * 店铺行业
     */
    private Boolean shopIndustry;

    /**
     * 店长
     */
    private String shopOwner;

    /**
     * 店铺绑定的手机
     */
    private String mobile;

    /**
     * 店铺联系电话
     */
    private String tel;

    /**
     * 店铺所在纬度
     */
    private String shopLat;

    /**
     * 店铺所在经度
     */
    private String shopLng;

    /**
     * 店铺详细地址
     */
    private String shopAddress;

    /**
     * 店铺所在省份
     */
    private String province;

    /**
     * 店铺所在城市
     */
    private String city;

    /**
     * 店铺所在区域
     */
    private String area;

    /**
     * 店铺省市区代码，用于回显
     */
    private String pcaCode;

    /**
     * 店铺LOGO
     */
    private String shopLogo;

    /**
     * 店铺相册
     */
    private String shopPhotos;

    /**
     * 店铺状态, 0：未开通, 1：营业中, 2：停业中
     */
    private Boolean shopStatus;

    /**
     * 满X包邮
     */
    private BigDecimal fullFreeShipping;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
