package com.xi.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.xi.domain.OrderItem;
import com.xi.domain.dto.OrderItemDto;
import com.xi.domain.dto.ShopCartDto;
import com.xi.domain.dto.ShopOrderDto;
import com.xi.mapper.OrderItemMapper;
import com.xi.service.OrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 订单项 服务实现类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-28
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

    @Override
    public void createOrderItem(String userId, String userAddrOrderId, ShopOrderDto shopOrderDto) {
        OrderItem orderItem = new OrderItem();
        ShopCartDto shopCartDto = shopOrderDto.getShopCartDto();

        orderItem.setUserId(userId);
        orderItem.setTotal(shopOrderDto.getTotal());
        orderItem.setActualTotal(shopOrderDto.getActualTotal());
        orderItem.setReduceAmount(shopOrderDto.getOrderReduce());
        orderItem.setShopId(shopCartDto.getShopId());
        orderItem.setShopName(shopCartDto.getShopName());
        orderItem.setAddrOrderId(userAddrOrderId);
        orderItem.setOrderSerialNumber(shopCartDto.getOrderItemDto().getOrderSerialNumber());
        orderItem.setProdCount(shopCartDto.getOrderItemDto().getTotalStocks());

        LocalDateTime now = LocalDateTime.now();
        orderItem.setCreateTime(now);
        orderItem.setUpdateTime(now);

        this.save(orderItem);
    }
}
