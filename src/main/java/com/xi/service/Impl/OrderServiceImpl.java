package com.xi.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xi.constant.SystemConstant;
import com.xi.domain.OrderDo;
import com.xi.domain.dto.BasketDto;
import com.xi.domain.dto.ProdDto;
import com.xi.domain.param.OrderParam;
import com.xi.enums.ResponseCodeEnum;
import com.xi.exception.BizException;
import com.xi.mapper.OrderMapper;
import com.xi.service.BasketService;
import com.xi.service.OrderService;
import com.xi.service.ProdService;
import com.xi.service.SkuService;
import jakarta.annotation.Resource;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-28
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderDo> implements OrderService {

    @Resource
    private SkuService skuService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private BasketService basketService;

    @Resource
    private ProdService prodService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void SubmitOrder(OrderParam orderParam) {
        // 乐观锁库存扣减
        ProdDto prodDto = prodService.getStocksAndVersion(orderParam.getProdId());
        prodService.updateStocksLock(orderParam.getProdId(), orderParam.getSkuId(), orderParam.getProdCount(), prodDto.getVersion(),
                prodDto.getSkuDtoMap().get(orderParam.getSkuId()).getVersion());


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

    @Override
    public void createOrderByCart(BasketDto basketDto) {
        OrderDo orderDo = BeanUtil.copyProperties(basketDto, OrderDo.class);
        orderDo.setCreateTime(LocalDateTime.now());
        orderDo.setUpdateTime(LocalDateTime.now());
        this.save(orderDo);
    }

}
