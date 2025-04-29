package com.xi.service.consumer;

import com.xi.domain.dto.BasketDto;
import com.xi.domain.param.OrderParam;

import java.util.List;

public interface OrderListener {


    /**
     * 处理购物车订单消息
     * @param basketDtoList 购物车列表
     */
    void createOrderAndUserAddrOrder(List<BasketDto> basketDtoList);

    /**
     * 处理单个订单消息
     * @param orderParam 订单消息
     */
    void createOrderAndUserAddrOrder(OrderParam orderParam);

}
