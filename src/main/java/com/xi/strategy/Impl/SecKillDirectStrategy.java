package com.xi.strategy.Impl;

import com.xi.entity.dto.SkuDto;
import com.xi.entity.param.OrderParam;
import com.xi.service.SkuService;
import com.xi.strategy.StockDecreaseStrategy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SecKillDirectStrategy implements StockDecreaseStrategy {

    @Resource
    private SkuService skuService;

    @Override
    public boolean decreaseStock(OrderParam orderParam) {
        // 乐观锁库存扣减
        SkuDto skuDto = skuService.getStocksAndVersionBySkuId(orderParam.getSkuId());
        return skuService.updateStocksLock(orderParam, skuDto.getVersion());
    }
}