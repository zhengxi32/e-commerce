package com.xi.service.Impl;

import com.xi.convert.SkuConvert;
import com.xi.domain.SkuDo;
import com.xi.domain.dto.SkuDto;
import com.xi.enums.ResponseCodeEnum;
import com.xi.exception.BizException;
import com.xi.mapper.SkuMapper;
import com.xi.service.SkuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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


    @Override
    @Cacheable(cacheNames = "SkuDto", key = "#skuId")
    public SkuDto getSkuDtoBySkuId(String skuId) {
        SkuDo skuDo = this.baseMapper.getSkuDoBySkuId(skuId);
        return SkuConvert.INSTANCE.SkuDoToDto(skuDo);
    }

    @Override
    public void updateStocks(String skuId, Integer stocks) {
        if (this.baseMapper.updateStocks(skuId, stocks) == 0) {
            throw new BizException(ResponseCodeEnum.STOCKS_NOT_ENOUGH);
        }
    }

    @Override
    public Integer updateStocksLock(String skuId, Integer stocks, Integer skuVersion) {
        return this.baseMapper.updateStocksLock(skuId, stocks, skuVersion);
    }

    @Override
    public Map<String, SkuDto> getStocksAndVersionByProdId(String prodId) {
        return this.baseMapper.getStocksAndVersionByProdId(prodId);
    }
}
