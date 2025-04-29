package com.xi.service.consumer.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.xi.domain.UserAddrOrderDo;
import com.xi.domain.dto.BasketDto;
import com.xi.domain.dto.UserAddrDto;
import com.xi.domain.param.OrderParam;
import com.xi.service.OrderItemService;
import com.xi.service.OrderService;
import com.xi.service.UserAddrOrderService;
import com.xi.service.UserAddrService;
import com.xi.service.consumer.OrderListener;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    private OrderItemService orderItemService;

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

                // 生成订单项
                basketDto.setUserId("userId");
                orderItemService.createOrderItemByCart(basketDto);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public void createOrderAndUserAddrOrder(OrderParam orderParam) {
        String OrderSerialNumber = IdUtil.getSnowflake().nextIdStr();
        UserAddrDto userAddrDto = StrUtil.isEmpty(orderParam.getAddrId()) ? userAddrService.getCommonAddr("userId") :
                userAddrService.getUserAddrDtoByUserIdAndAddrId("userId", orderParam.getAddrId());

    }
}
