package com.xi.service.Impl;

import com.xi.domain.Order;
import com.xi.mapper.OrderMapper;
import com.xi.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-28
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

}
