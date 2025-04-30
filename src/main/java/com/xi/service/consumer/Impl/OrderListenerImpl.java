package com.xi.service.consumer.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.xi.convert.OrderConvert;
import com.xi.domain.OrderDo;
import com.xi.domain.UserAddrOrderDo;
import com.xi.domain.dto.BasketDto;
import com.xi.domain.dto.OrderDto;
import com.xi.domain.dto.UserAddrDto;
import com.xi.domain.param.OrderParam;
import com.xi.service.OrderService;
import com.xi.service.UserAddrOrderService;
import com.xi.service.UserAddrService;
import com.xi.service.consumer.OrderListener;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderListenerImpl implements OrderListener {

    @Resource
    private UserAddrService userAddrService;

    @Resource
    private UserAddrOrderService userAddrOrderService;

    @Resource
    private OrderService orderService;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void createOrderAndUserAddrOrder(List<BasketDto> basketDtoList) {
        Map<String, List<BasketDto>> basketDtoMap = basketDtoList.stream().collect(Collectors.groupingBy(BasketDto::getShopId));
        for (Map.Entry<String, List<BasketDto>> entry : basketDtoMap.entrySet()) {
            String OrderSerialNumber = IdUtil.getSnowflake().nextIdStr();

            for (BasketDto basketDto : entry.getValue()) {
                // 订单地址保存
                UserAddrDto userAddrDto = StrUtil.isEmpty(basketDto.getAddrId()) ? userAddrService.getCommonAddr("userId") :
                        userAddrService.getUserAddrDtoByUserIdAndAddrId("userId", basketDto.getAddrId());
                UserAddrOrderDo userAddrOrderDo = BeanUtil.copyProperties(userAddrDto, UserAddrOrderDo.class);
                userAddrOrderDo.setOrderSerialNumber(OrderSerialNumber);
                basketDto.setAddrOrderId(String.valueOf(userAddrOrderService.getBaseMapper().insert(userAddrOrderDo)));

                // 生成订单
                basketDto.setUserId("userId");
                orderService.createOrderByCart(basketDto);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void createOrderAndUserAddrOrder(OrderParam orderParam) {
        String OrderSerialNumber = IdUtil.getSnowflake().nextIdStr();

        // 订单地址保存
        UserAddrDto userAddrDto = StrUtil.isEmpty(orderParam.getAddrId()) ? userAddrService.getCommonAddr("userId") :
                userAddrService.getUserAddrDtoByUserIdAndAddrId("userId", orderParam.getAddrId());
        UserAddrOrderDo userAddrOrderDo = BeanUtil.copyProperties(userAddrDto, UserAddrOrderDo.class);
        userAddrOrderDo.setOrderSerialNumber(OrderSerialNumber);
        orderParam.setOrderSerialId(String.valueOf(userAddrOrderService.getBaseMapper().insert(userAddrOrderDo)));

        // 生成订单
        OrderDto orderDto = OrderConvert.INSTANCE.OrderParamToDto(orderParam);
        OrderDo orderDo = OrderConvert.INSTANCE.OrderDtoToDo(orderDto);
        orderDo.setUserId("userId");
        orderDo.setCreateTime(LocalDateTime.now());
        orderDo.setUpdateTime(LocalDateTime.now());
        orderService.save(orderDo);
    }

}
