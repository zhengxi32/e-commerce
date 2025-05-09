package com.xi.controller;

import com.xi.common.Response;
import com.xi.entity.param.OrderParam;
import com.xi.service.OrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private OrderService orderService;

    @PostMapping("/submitOrder")
    public Response<Void> submitOrder(@RequestBody @Valid OrderParam orderParam) {
        orderService.submitOrder(orderParam);
        return Response.success();
    }

}
