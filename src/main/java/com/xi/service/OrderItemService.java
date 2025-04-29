package com.xi.service;

import com.xi.domain.OrderItemDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xi.domain.dto.BasketDto;
import com.xi.domain.dto.ShopOrderDto;

/**
 * <p>
 * 订单项 服务类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-28
 */
public interface OrderItemService extends IService<OrderItemDo> {

    /**
     * 单个商品下单订单项保存
     * @param userId 用户ID
     * @param userAddrOrderId 订单地址ID
     * @param shopOrderDto 订单商品信息
     */
    void createOrderItem(String userId, String userAddrOrderId, ShopOrderDto shopOrderDto);

    /**
     * 根据购物车信息生成订单项
     */
    void createOrderItemByCart(BasketDto basketDto);

}
