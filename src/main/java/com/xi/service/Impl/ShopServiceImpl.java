package com.xi.service.Impl;

import com.xi.convert.ShopConvert;
import com.xi.entity.tb.ShopDo;
import com.xi.entity.dto.ShopDto;
import com.xi.mapper.ShopMapper;
import com.xi.service.ShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 店铺表 服务实现类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-27
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, ShopDo> implements ShopService {

    @Override
    @Cacheable(value = "shopDto", key = "#shopId")
    public ShopDto getShopDtoByShopId(String shopId) {
        ShopDo shopDo = this.baseMapper.getShopDtoByShopId(shopId);
        return ShopConvert.INSTANCE.ShopDoToShopDto(shopDo);
    }
}
