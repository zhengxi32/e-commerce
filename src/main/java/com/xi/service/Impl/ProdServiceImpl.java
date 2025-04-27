package com.xi.service.Impl;

import com.xi.domain.Prod;
import com.xi.mapper.ProdMapper;
import com.xi.service.ProdService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class ProdServiceImpl extends ServiceImpl<ProdMapper, Prod> implements ProdService {

    @Override
    public void updateStock(String prodId) {
        Integer version = this.baseMapper.selectById(prodId).getVersion();
        this.baseMapper.updateStock(prodId, version);
    }
}
