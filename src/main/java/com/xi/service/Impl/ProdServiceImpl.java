package com.xi.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xi.convert.ProdConvert;
import com.xi.entity.tb.ProdDo;
import com.xi.entity.dto.ProdDto;
import com.xi.mapper.ProdMapper;
import com.xi.service.ProdService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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

    @Override
    @Cacheable(cacheNames = "prodDto", key = "#prodId")
    public ProdDto getProdDtoByProdId(String prodId) {
        ProdDo prodDo = this.baseMapper.getProdDtoByProdId(prodId);
        return ProdConvert.INSTANCE.ProdDoToDto(prodDo);
    }

}
