package com.xi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xi.domain.OrderDo;
import com.xi.domain.dto.BasketDto;
import com.xi.domain.param.OrderParam;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-28
 */
public interface OrderService extends IService<OrderDo> {

    /**
     * 单个商品提交订单
     * @param orderParam 订单参数
     */
    void SubmitOrder(OrderParam orderParam);

    /**
     * 购物车提交订单
     * @param orderParam 订单参数
     */
    void submitBasketOrder(OrderParam orderParam);

    /**
     * 生成订单
     * @param basketDto 购物车参数
     */
    void createOrderByCart(BasketDto basketDto);

    /**
     * 提交订单 秒杀场景
     * @param orderParam 订单信息
     */
    void submitOrderInSecKill(OrderParam orderParam);

    /**
     * 提交购物车订单 秒杀场景
     * @param orderParam 订单信息
     */
    void submitBasketOrderInSecKill(OrderParam orderParam);

}
