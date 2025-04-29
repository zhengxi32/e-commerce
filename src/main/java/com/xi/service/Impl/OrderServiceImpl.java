package com.xi.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.xi.constant.SystemConstant;
import com.xi.domain.Order;
import com.xi.domain.UserAddrOrderDo;
import com.xi.domain.dto.*;
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
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Resource
    private SkuService skuService;

    @Resource
    private ShopService shopService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private BasketService basketService;

    @Resource
    private ProdService prodService;


    @Resource
    private UserAddrOrderService userAddrOrderService;

    @Override
    @CachePut(cacheNames = "orderCache", key = "#userId + ':' + #orderSerialId")
    public ShopOrderDto createOrder(String userId, String orderSerialId, OrderParam orderParam) {
        ShopOrderDto shopOrderDto = new ShopOrderDto();

        UserAddrDto commonAddr = userAddrService.getCommonAddr(userId);
        SkuDto skuDto = skuService.getSkuDtoBySkuId(orderParam.getSkuId());
        BigDecimal value = ArithUtil.mul(skuDto.getPrice(), new BigDecimal(orderParam.getProdCount()));

        shopOrderDto.setValue(value);
        shopOrderDto.setActualValue(value);
        shopOrderDto.setUserAddrDto(commonAddr);
        shopOrderDto.setOrderSerialId(orderSerialId);
        shopOrderDto.setTotalCount(orderParam.getProdCount());

        ShopCartDto shopCartDto = new ShopCartDto();
        shopCartDto.setShopId(orderParam.getShopId());
        shopCartDto.setShopName(orderParam.getShopName());

        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setProdId(orderParam.getProdId());
        orderItemDto.setAddrId(orderParam.getAddrId());
        orderItemDto.setTotalStocks(orderParam.getProdCount());
        orderItemDto.setStocks(orderParam.getProdCount());
        orderItemDto.setCost(value);
        orderItemDto.setActualCost(value);

        shopOrderDto.setShopCartDto(shopCartDto);
        shopCartDto.setOrderItemDto(orderItemDto);

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
        prodService.updateStocks(orderItemDto.getProdId(), orderItemDto.getTotalStocks());
        skuService.updateStocks(orderItemDto.getSkuId(), orderItemDto.getStocks());

        // 订单地址保存
        UserAddrDto userAddrDto = userAddrService.getUserAddrDtoByUserIdAndAddrId(userId, orderItemDto.getAddrId());
        UserAddrOrderDo userAddrOrder = BeanUtil.copyProperties(userAddrDto, UserAddrOrderDo.class);
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

    @Transactional(rollbackFor = Exception.class)
    public void SubmitOrder(OrderParam orderParam) {
        // 乐观锁库存扣减
        ProdDto prodDto = prodService.getStocksAndVersion(orderParam.getProdId());
        prodService.updateStocksLock(orderParam.getProdId(), orderParam.getSkuId(), orderParam.getProdCount(), prodDto.getVersion(),
                prodDto.getSkuDtoMap().get(orderParam.getSkuId()).getVersion());

        //
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitBasketOrder(OrderParam orderParam) {

        List<BasketDto> basketDtoList = orderParam.getBasketDtoList();
        // 用户ID todo

        // Redis库存预检查
        for (BasketDto basketDto : basketDtoList) {
            if (skuService.getSkuDtoBySkuId(basketDto.getSkuId()).getStocks() < basketDto.getStocks()) {
                throw new BizException(ResponseCodeEnum.STOCKS_NOT_ENOUGH);
            }
        }

        // Redisson分布式锁
        RLock[] rLocks = new RLock[basketDtoList.size()];

        // SkuID排序 避免死锁
        basketService.sortListBySkuIdAsc(basketDtoList);

        for (BasketDto basketDto : basketDtoList) {
            rLocks[0] = redissonClient.getLock(SystemConstant.LOCK + basketDto.getSkuId());
        }
        RedissonMultiLock redissonMultiLock = new RedissonMultiLock(rLocks);

        try {
            // 设置锁的等待时间和超时时间
            boolean acquired = redissonMultiLock.tryLock(500, 30000, TimeUnit.MILLISECONDS);

            // 获取到锁 业务执行
            if (acquired) {
                for (BasketDto basketDto : basketDtoList) {
                    // 乐观锁库存扣减
                    ProdDto prodDto = prodService.getStocksAndVersion(basketDto.getProdId());
                    prodService.updateStocksLock(basketDto.getProdId(), basketDto.getSkuId(), basketDto.getStocks(), prodDto.getVersion(),
                            prodDto.getSkuDtoMap().get(basketDto.getSkuId()).getVersion());
                }

                // 发送订单消息至kafka todo

                for (RLock rLock : rLocks) rLock.unlock();
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        } finally {
            // 释放锁
            redissonMultiLock.unlock();
        }

    }

}
