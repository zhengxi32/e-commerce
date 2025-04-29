package com.xi.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.xi.domain.OrderItemDo;
import com.xi.domain.dto.BasketDto;
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
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItemDo> implements OrderItemService {

    @Override
    public void createOrderItem(String userId, String userAddrOrderId, ShopOrderDto shopOrderDto) {
        OrderItemDo orderItem = new OrderItemDo();
        ShopCartDto shopCartDto = shopOrderDto.getShopCartDto();
        OrderItemDto orderItemDto = shopCartDto.getOrderItemDto();

        orderItem.setUserId(userId);
        orderItem.setCost(orderItemDto.getCost());
        orderItem.setActualCost(orderItemDto.getActualCost());
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

    @Override
    public void createOrderItemByCart(BasketDto basketDto) {
        OrderItemDo orderItemDo = BeanUtil.copyProperties(basketDto, OrderItemDo.class);
        orderItemDo.setCreateTime(LocalDateTime.now());
        orderItemDo.setUpdateTime(LocalDateTime.now());
        this.save(orderItemDo);
    }

}
