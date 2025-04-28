package com.xi.service;

import com.xi.domain.OrderItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xi.domain.dto.ShopOrderDto;

/**
 * <p>
 * 订单项 服务类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-28
 */
public interface OrderItemService extends IService<OrderItem> {

    void createOrderItem(String userId, String userAddrOrderId, ShopOrderDto shopOrderDto);
}
