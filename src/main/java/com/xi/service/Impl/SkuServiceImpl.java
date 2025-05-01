package com.xi.service.Impl;

import com.xi.constant.RedisConstant;
import com.xi.convert.SkuConvert;
import com.xi.domain.SkuDo;
import com.xi.domain.dto.SkuDto;
import com.xi.enums.ResponseCodeEnum;
import com.xi.exception.BizException;
import com.xi.mapper.SkuMapper;
import com.xi.service.SkuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品规格表 服务实现类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-27
 */
@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, SkuDo> implements SkuService {

    @Resource
    private RedissonClient redissonClient;

    @Override
    @Cacheable(cacheNames = "SkuDto", key = "#skuId")
    public SkuDto getSkuDtoBySkuId(String skuId) {
        SkuDo skuDo = this.baseMapper.getSkuDoBySkuId(skuId);
        return SkuConvert.INSTANCE.SkuDoToDto(skuDo);
    }

    @Override
    public void updateStocksLock(String skuId, Integer stocks, Integer skuVersion) {
        Integer result = this.baseMapper.updateStocksLock(skuId, stocks, skuVersion);

        if (result == 0) {
            throw new BizException(ResponseCodeEnum.STOCKS_NOT_ENOUGH);
        }
        // kafka异步更新缓存 todo
    }

    @Override
    public SkuDto getStocksAndVersionBySkuId(String skuId) {
        RMap<String, Integer> map = redissonClient.getMap(RedisConstant.getSkuKey(skuId));

        SkuDto skuDto = new SkuDto();

        if (map.isEmpty()) {
            SkuDo skuDo = this.baseMapper.getStocksAndVersionBySkuId(skuId);
            skuDto = SkuConvert.INSTANCE.SkuDoToDto(skuDo);
            // 异步更新缓存 todo
        } else {
            skuDto.setStocks(map.get("stocks"));
            skuDto.setVersion(map.get("version"));
        }
        return skuDto;
    }

}
