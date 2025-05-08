package com.xi.scheduler;

import com.xi.constant.RedisConstant;
import com.xi.entity.dto.SkuDto;
import com.xi.service.SkuService;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class redisTask {

    @Resource
    private SkuService skuService;

    @Resource
    private RedissonClient redissonClient;

    @XxlJob("stockRefresh")
    public void stockRefresh() {
        log.info("库存同步定时任务开始执行，执行时间: {}", LocalDateTime.now());
        // 获取所有库存和版本信息
        List<SkuDto> stocksAndVersionAll = skuService.getStocksAndVersionAll();
        // 遍历所有库存和版本信息
        for (SkuDto skuDto : stocksAndVersionAll) {
            // 获取库存锁
            RLock rLock = redissonClient.getLock(skuDto.getSkuId());
            try {
                // 尝试获取锁，等待时间为5秒，超时时间为3秒
                boolean acquired = rLock.tryLock(5, 3000, TimeUnit.MILLISECONDS);
                // 如果获取到锁
                if (acquired) {
                    // 获取库存和版本信息
                    RMap<String, Integer> rMap = redissonClient.getMap(skuDto.getSkuId());
                    String version = RedisConstant.VERSION + skuDto.getSkuId();
                    String stocks = RedisConstant.STOCKS + skuDto.getSkuId();
                    // 版本号落后 进行刷新
                    if (rMap.get(version) < skuDto.getVersion()) {
                        rMap.put(version, skuDto.getVersion());
                        rMap.put(stocks, skuDto.getStocks());
                    }
                } else log.warn("sku {} 库存同步失败，请重试", skuDto.getSkuId());

            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }

        }
    }

}
