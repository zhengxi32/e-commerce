package com.xi.strategy.Impl;

import cn.hutool.core.util.IdUtil;
import com.xi.constant.OrderTagConstant;
import com.xi.constant.RedisConstant;
import com.xi.entity.dto.SkuDto;
import com.xi.entity.param.OrderParam;
import com.xi.enums.ResponseCodeEnum;
import com.xi.exception.BizException;
import com.xi.service.OrderService;
import com.xi.service.SkuService;
import com.xi.strategy.StockDecreaseStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
public class SecKillDirectStrategy implements StockDecreaseStrategy {

    @Resource
    private SkuService skuService;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private OrderService orderService;

    @Override
    public void createOrder(OrderParam orderParam) {
        // 订单流水号
        orderParam.setOrderSerialNumberList(Collections.singletonList(IdUtil.getSnowflake().nextIdStr()));
        // 创建订单
        orderService.createOrderAndUserAddrOrder(orderParam);

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
        Integer re = script.evalSha(
                RScript.Mode.READ_WRITE,
                sha1,
                RScript.ReturnType.INTEGER,
                Collections.singletonList(RedisConstant.getSkuKey(orderParam.getSkuId())),
                RedisConstant.STOCKS,
                orderParam.getProdCount(),
                RedisConstant.VERSION
        );

        // 预扣减失败
        if (re == 0) {
            throw new BizException(ResponseCodeEnum.STOCKS_NOT_ENOUGH);
        }

    }

    @Override
    public boolean decreaseStock(OrderParam orderParam) {
        // 乐观锁库存扣减
        SkuDto skuDto = skuService.getStocksAndVersionBySkuId(orderParam.getSkuId());
        return skuService.updateStocksLock(orderParam, skuDto.getVersion());
    }
}