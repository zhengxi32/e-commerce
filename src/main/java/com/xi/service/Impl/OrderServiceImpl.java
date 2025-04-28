package com.xi.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.xi.domain.Order;
import com.xi.domain.UserAddr;
import com.xi.domain.UserAddrOrder;
import com.xi.domain.dto.OrderItemDto;
import com.xi.domain.dto.ShopCartDto;
import com.xi.domain.dto.ShopOrderDto;
import com.xi.domain.dto.UserAddrDto;
import com.xi.domain.param.OrderParam;
import com.xi.enums.ResponseCodeEnum;
import com.xi.exception.BizException;
import com.xi.mapper.OrderMapper;
import com.xi.mapper.ProdMapper;
import com.xi.mapper.SkuMapper;
import com.xi.mapper.UserAddrOrderMapper;
import com.xi.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xi.util.ArithUtil;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Resource
    private ProdMapper prodMapper;

    @Resource
    private SkuMapper skuMapper;

    @Resource
    private UserAddrService userAddrService;

    @Resource
    private UserAddrOrderMapper userAddrOrderMapper;

    @Resource
    private OrderItemService orderItemService;


    @Override
    @CachePut(cacheNames = "orderCache", key = "#userId + ':' + #orderSerialId")
    public ShopOrderDto createOrder(String userId, String orderSerialId, OrderParam orderParam) {
        ShopOrderDto shopOrderDto = new ShopOrderDto();

        UserAddrDto commonAddr = userAddrService.getCommonAddr(userId);
        OrderItemDto orderItemDto = shopOrderDto.getShopCartDto().getOrderItemDto();
        BigDecimal value = ArithUtil.mul(orderItemDto.getPrice(), new BigDecimal(orderItemDto.getStocks()));

        shopOrderDto.setValue(value);
        shopOrderDto.setActualValue(value);
        shopOrderDto.setUserAddrDto(commonAddr);
        shopOrderDto.setOrderSerialId(orderSerialId);
        shopOrderDto.setTotalCount(orderParam.getProdCount());

        return shopOrderDto;
    }

    @Override
    @Cacheable(cacheNames = "orderCache", key = "#userId + ':' + #orderSerialId")
    public ShopOrderDto getOrderCache(String userId, String orderSerialId) {
        return null;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitOrder(String userId, ShopOrderDto shopOrderDto) {
        // 获取商品信息
        OrderItemDto orderItemDto = shopOrderDto.getShopCartDto().getOrderItemDto();

        // 库存扣减
        if (prodMapper.updateStocks(orderItemDto.getProdId(), orderItemDto.getTotalStocks()) == 0) {
            throw new BizException(ResponseCodeEnum.STOCKS_NOT_ENOUGH);
        }
        if (skuMapper.updateStocks(orderItemDto.getSkuId(), orderItemDto.getStocks()) == 0) {
            throw new BizException(ResponseCodeEnum.STOCKS_NOT_ENOUGH);
        }

        // 订单地址保存
        UserAddr userAddr = userAddrService.getUserAddrByUserIdAndAddrId(userId, orderItemDto.getAddrId());
        UserAddrOrder userAddrOrder = BeanUtil.copyProperties(userAddr, UserAddrOrder.class);
        String userAddrOrderId = String.valueOf(userAddrOrderMapper.insert(userAddrOrder));


        // 订单项保存
        orderItemService.createOrderItem(userId, userAddrOrderId, shopOrderDto);

        // 订单保存
        Order order = new Order();
        order.setOrderSerialId(shopOrderDto.getOrderSerialId());
        order.setTotalCount(orderItemDto.getTotalStocks());
        LocalDateTime now = LocalDateTime.now();
        order.setCreateTime(now);
        order.setUpdateTime(now);

        // 单个商品下单 总价值计算当前商品即可
        order.setValue(orderItemDto.getCost());
        order.setActualValue(orderItemDto.getActualCost());

        this.baseMapper.insert(order);

        // CDC异步更新Redis
    }

}
