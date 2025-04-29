package com.xi.service.Impl;

import com.xi.convert.ProdConvert;
import com.xi.domain.ProdDo;
import com.xi.domain.SkuDo;
import com.xi.domain.dto.ProdDto;
import com.xi.domain.dto.SkuDto;
import com.xi.enums.ResponseCodeEnum;
import com.xi.exception.BizException;
import com.xi.mapper.ProdMapper;
import com.xi.service.ProdService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xi.service.SkuService;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-24
 */
@Service
public class ProdServiceImpl extends ServiceImpl<ProdMapper, ProdDo> implements ProdService {

    @Resource
    private SkuService skuService;

    @Override
    @Cacheable(cacheNames = "prodDto", key = "#prodId")
    public ProdDto getProdDtoByProdId(String prodId) {
        ProdDo prodDo = this.baseMapper.getProdDtoByProdId(prodId);
        return ProdConvert.INSTANCE.ProdDoToDto(prodDo);
    }

    @Override
    public void updateStocks(String prodId, Integer totalStocks) {
        if (this.baseMapper.updateStocks(prodId, totalStocks) == 0) {
            throw new BizException(ResponseCodeEnum.STOCKS_NOT_ENOUGH);
        }
    }

    @Override
    public void updateStocksLock(String prodId, String skuId, Integer totalStocks, Integer prodVersion, Integer skuVersion) {
        Integer o1 = this.baseMapper.updateStocksLock(prodId, totalStocks, prodVersion);
        Integer o2 = skuService.updateStocksLock(skuId, totalStocks, skuVersion);
        if (o1 == 0 || o2 == 0) {
            throw new BizException(ResponseCodeEnum.STOCKS_NOT_ENOUGH);
        }
    }

    @Override
    @Cacheable(cacheNames = "prodAndSkuStock", key = "#prodId")
    public ProdDto getStocksAndVersion(String prodId) {
        ProdDo prodDo = this.baseMapper.getTotalStocksAndVersionByProdId(prodId);
        ProdDto prodDto = ProdConvert.INSTANCE.ProdDoToDto(prodDo);

        Map<String, SkuDto> skuDtoMap = skuService.getStocksAndVersionByProdId(prodId);
        prodDto.setSkuDtoMap(skuDtoMap);

        return prodDto;
    }
}
