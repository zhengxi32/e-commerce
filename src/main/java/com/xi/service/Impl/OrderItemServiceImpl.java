package com.xi.service.Impl;

import com.xi.domain.OrderItem;
import com.xi.mapper.OrderItemMapper;
import com.xi.service.OrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
