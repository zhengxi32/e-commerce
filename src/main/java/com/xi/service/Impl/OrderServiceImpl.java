package com.xi.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xi.annotation.RedisLock;
import com.xi.constant.RedisConstant;
import com.xi.constant.SystemConstant;
import com.xi.domain.OrderDo;
import com.xi.domain.dto.BasketDto;
import com.xi.domain.dto.SkuDto;
import com.xi.domain.param.OrderParam;
import com.xi.enums.ResponseCodeEnum;
import com.xi.exception.BizException;
import com.xi.mapper.OrderMapper;
import com.xi.service.BasketService;
import com.xi.service.OrderService;
import com.xi.service.SkuService;
import jakarta.annotation.Resource;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
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

    @Resource(name = "secKillStockDeductThreadPool")
    private ExecutorService executorService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    @RedisLock(key = "#orderParam.skuId")
    public void SubmitOrder(OrderParam orderParam) {
        // 乐观锁库存扣减
        SkuDto skuDto = skuService.getStocksAndVersionBySkuId(orderParam.getSkuId());
        skuService.updateStocksLock(orderParam.getSkuId(), orderParam.getProdCount(), skuDto.getVersion());

        // 发送订单消息至kafka todo

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitBasketOrder(OrderParam orderParam) {
        List<BasketDto> basketDtoList = orderParam.getBasketDtoList();

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
            boolean acquired = redissonMultiLock.tryLock(50, 3000, TimeUnit.MILLISECONDS);

            // 获取到锁 业务执行
            if (acquired) {
                for (BasketDto basketDto : basketDtoList) {
                    // 乐观锁库存扣减
                    SkuDto skuDto = skuService.getStocksAndVersionBySkuId(basketDto.getSkuId());
                    skuService.updateStocksLock(basketDto.getSkuId(), basketDto.getStocks(), skuDto.getVersion());
                }

                // 发送订单消息至kafka todo

                for (RLock rLock : rLocks) rLock.unlock();
            }
        } catch (InterruptedException e) {
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

    @Override
    public void submitOrderInSecKill(OrderParam orderParam) {
        // Redis原子扣减
        RScript script = redissonClient.getScript();

        String skuStocksDeduct = """
                local skuStocks = redis.call('HGET', KEYS[1], ARGV[1])
                if tonumber(skuStocks) >= tonumber(ARGV[2]) then
                    redis.call('HINCRBY', KEYS[1], ARGV[1], -ARGV[2])
                    redis.call("HINCRBY', KEYS[1], ARGV[3], 1)
                    return 1
                else
                    return 0
                end
                """;

        String sha1 = script.scriptLoad(skuStocksDeduct);
        script.evalSha(
                RScript.Mode.READ_WRITE,
                sha1,
                RScript.ReturnType.INTEGER,
                Collections.singletonList(RedisConstant.getSkuKey(orderParam.getSkuId())),
                RedisConstant.STOCKS,
                orderParam.getProdCount(),
                RedisConstant.VERSION
        );

        String prodStocksDeduct = """
                local prodStocks = redis.call('HGET', KEYS[1], ARGV[1])
                if tonumber(prodStocks) >= tonumber(ARGV[2]) then
                    redis.call('HINCRBY', KEYS[1], ARGV[1], -ARGV[2])
                    redis.call('HINCRBY', KEYS[1], ARGV[3], 1)
                    return 1
                else
                    return 0
                end
                """;
        String sha2 = script.scriptLoad(prodStocksDeduct);
        script.evalSha(
                RScript.Mode.READ_WRITE,
                sha2,
                RScript.ReturnType.INTEGER,
                Collections.singletonList(RedisConstant.getSkuKey(orderParam.getProdId())),
                orderParam.getProdCount(),
                RedisConstant.VERSION
        );

        // kafka处理库存扣减与订单生成 todo
    }

    @Override
    public void submitBasketOrderInSecKill(OrderParam orderParam) {

    }

}
