package com.xi.controller;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.xi.common.Response;
import com.xi.domain.dto.OrderItemDto;
import com.xi.domain.dto.ShopOrderDto;
import com.xi.domain.param.OrderItemParam;
import com.xi.domain.param.OrderParam;
import com.xi.domain.param.SubmitOrderParam;
import com.xi.enums.ResponseCodeEnum;
import com.xi.exception.BizException;
import com.xi.service.BasketService;
import com.xi.service.OrderService;
import com.xi.service.UserAddrService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author 郑西
 * @since 2025-04-24
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private UserAddrService userAddrService;

    @Resource
    private OrderService orderService;

    @Resource
    private BasketService basketService;

    @PostMapping("/createOrder")
    public Response<ShopOrderDto> createOrder(@RequestBody OrderParam orderParam) {
        // 获取用户信息
        String userId = "";

        // 确认订单
        if (StrUtil.isEmpty(orderParam.getOrderSerialId())) {
            orderParam.setOrderSerialId(IdUtil.randomUUID());
        }

        // 构建返回体
        ShopOrderDto shopOrderDto = orderService.createOrder(userId, orderParam.getOrderSerialId(), orderParam);

        return Response.success(shopOrderDto);
    }

    @PostMapping("/submitOrder")
    public Response<Void> submitOrder(@RequestBody SubmitOrderParam submitOrderParam) {
        String userId = "";

        // 判断订单是否过期
        ShopOrderDto shopOrderDto = orderService.getOrderCache(userId, submitOrderParam.getOrderSerialId());
        if (ObjUtil.isEmpty(shopOrderDto)) {
            throw new BizException(ResponseCodeEnum.ORDER_EXPIRED);
        }

        // 插入备注
        Map<String, String> prodRemarkMap = submitOrderParam.getOrderItemParamList().stream().collect(Collectors.toMap(OrderItemParam::getProdId, OrderItemParam::getRemarks));
        for (OrderItemDto orderItemDto : shopOrderDto.getShopCartDto().getOrderItemDtoList()) {
            orderItemDto.setRemarks(prodRemarkMap.get(orderItemDto.getProdId()));
        }

        // 提交订单
        orderService.submitOrder(userId, shopOrderDto);

        return Response.success();
    }

    @PostMapping("/createBasketOrder")
    public Response<Void> createBasketOrder(@RequestBody OrderParam orderParam) {
        String userId = "";




        // 构建返回体


        return Response.success();
    }

    @PostMapping("/submitBasketOrder")
    public Response<Void> submitBasketOrder(@RequestBody OrderParam orderParam) {
        String userId = "";

        orderService.submitBasketOrder(orderParam.getBasketDtoList(), userId);

        // 检查Redis库存 （库存不足返回）

        // 分布式锁

        // 数据库获取商品库存+版本号（库存不足返回）

        // 乐观锁扣减库存（事务）

        // 事务完成监听器 处理订单项

        // 释放锁

        // 备注填入各订单项

        // 保存订单地址（返回addr_order_id）

        // 构建订单项（缓存）

        // CDC更新Redis库存与版本号

        return Response.success();
    }
}
