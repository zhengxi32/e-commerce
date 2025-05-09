package com.xi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xi.entity.dto.BasketDto;
import com.xi.entity.dto.OrderDto;
import com.xi.entity.param.OrderParam;
import com.xi.entity.tb.OrderDo;

import java.util.List;

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
     * 提交订单
     *
     * @param orderParam 订单参数
     */
    void submitOrder(OrderParam orderParam);

    /**
     * 生成订单
     *
     * @param basketDto 购物车参数
     */
    void createOrderByCart(BasketDto basketDto);

    /**
     * 处理购物车订单消息
     *
     * @param basketDtoList 购物车列表
     */
    void createOrderAndUserAddrOrder(List<BasketDto> basketDtoList);

    /**
     * 处理单个订单消息
     *
     * @param orderParam 订单消息
     */
    void createOrderAndUserAddrOrder(OrderParam orderParam);

    /**
     * 购物车下单失败场景 回滚订单 返还优惠券信息
     *
     * @param orderSerialNumber 订单ID
     */
    void rollbackOrder(List<String> orderSerialNumber);

    /**
     * 过期订单失效
     */
    void batchUpdateStatus(List<String> orderIdList);

    /**
     * 获取超时订单
     */
    List<OrderDto> getTimeoutOrders();

}
