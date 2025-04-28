package com.xi.controller;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.xi.common.Response;
import com.xi.domain.UserAddr;
import com.xi.domain.param.OrderParam;
import com.xi.service.UserAddrService;
import org.springframework.web.bind.annotation.PostMapping;
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

    private UserAddrService userAddrService;

    @PostMapping
    public Response<Void> createOrder(OrderParam orderParam) {
        String userId = "";
        // todo

        UserAddr userAddr = userAddrService.getUserAddrByUserId(userId, orderParam.getAddrId());

        // 库存扣减


        // 订单建立


        return Response.success();

    }

    @PostMapping
    public Response<Void> createBasketOrder(OrderParam orderParam) {



        return Response.success();
    }
}
