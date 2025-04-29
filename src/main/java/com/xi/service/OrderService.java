package com.xi.service;

import com.xi.domain.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xi.domain.dto.BasketDto;
import com.xi.domain.dto.ShopCartDto;
import com.xi.domain.dto.ShopOrderDto;
import com.xi.domain.param.OrderParam;

import java.util.List;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-28
 */
public interface OrderService extends IService<Order> {

    /**
     * 创建单个商品订单
     * @param userId 用户ID
     * @param orderSerialId 订单流水号
     * @param orderParam 订单参数
     * @return 订单信息
     */
    ShopOrderDto createOrder(String userId, String orderSerialId, OrderParam orderParam);

    /**
     * 获取订单缓存
     * @param userId 用户ID
     * @param orderSerialId 订单流水号
     * @return 订单信息
     */
    ShopOrderDto getOrderCache(String userId, String orderSerialId);

    /**
     * 单个商品下单
     * @param userId 用户ID
     * @param shopOrderDto 订单维度商品信息
     */
    void submitOrder(String userId, ShopOrderDto shopOrderDto);

    void submitBasketOrder(List<BasketDto> basketDtoList, String userId);
}
