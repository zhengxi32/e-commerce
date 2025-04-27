package com.xi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author 郑西
 * @since 2025-04-24
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("tb_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;

    /**
     * 店铺Id
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 订购用户ID
     */
    private String userId;

    /**
     * 产品名称, 多个产品将会以逗号隔开
     */
    private String prodName;

    /**
     * 订购流水号
     */
    private String orderNumber;

    /**
     * 总值
     */
    private BigDecimal total;

    /**
     * 实际总值
     */
    private BigDecimal actualTotal;

    /**
     * 支付方式, 0：手动代付, 1：微信支付, 2：支付宝
     */
    private Integer payType;

    /**
     * 订单备注
     */
    private String remarks;

    /**
     * 订单状态, 1：待付款, 2：待发货, 3：待收货, 4：待评价, 5：成功, 6：失败
     */
    private Integer status;

    /**
     * 配送方式ID
     */
    private Long dvyId;

    /**
     * 配送类型, 1：物流配送, 2：无需配送
     */
    private String dvyType;

    /**
     * 物流单号
     */
    private String dvyFlowId;

    /**
     * 发货时间
     */
    private LocalDateTime dvyTime;

    /**
     * 订单运费
     */
    private BigDecimal freightAmount;

    /**
     * 用户订单地址Id
     */
    private Long addrOrderId;

    /**
     * 订单商品总数
     */
    private Integer productNums;

    /**
     * 订购时间
     */
    private LocalDateTime createTime;

    /**
     * 订单更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 付款时间
     */
    private LocalDateTime payTime;

    /**
     * 完成时间
     */
    private LocalDateTime finallyTime;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 是否已支付, 1：已支付, 0：未支付
     */
    private Boolean isPayed;

    /**
     * 用户订单删除状态, 0：未删除, 1：已删除
     */
    private Integer isDelete;

    /**
     * 优惠总额
     */
    private BigDecimal reduceAmount;

    /**
     * 订单关闭原因, 1：超时未支付, 2：退款关闭, 3：买家取消, 4：已完成
     */
    private Boolean closeType;
}
