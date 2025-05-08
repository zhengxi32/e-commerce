package com.xi.service.Impl;

import cn.hutool.core.util.ObjUtil;
import com.xi.constant.SystemConstant;
import com.xi.entity.tb.BasketDo;
import com.xi.entity.dto.BasketDto;
import com.xi.entity.dto.ProdDto;
import com.xi.entity.dto.SkuDto;
import com.xi.mapper.BasketMapper;
import com.xi.service.BasketService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xi.service.ProdService;
import com.xi.service.ShopService;
import com.xi.service.SkuService;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * <p>
 * 购物车 服务实现类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-27
 */
@Service
public class BasketServiceImpl extends ServiceImpl<BasketMapper, BasketDo> implements BasketService {

    @Resource
    private ProdService prodService;

    @Resource
    private SkuService skuService;

    @Resource
    private ShopService shopService;



    @Override
    @Cacheable(cacheNames = "BasketDtoList", key = "#userId")
    public List<BasketDto> getBasketListByUserId(String userId) {
        List<BasketDto> basketDtoList = this.baseMapper.getBasketListByUserId(userId);

        for (BasketDto basketDto : basketDtoList) {
            ProdDto prodDto = prodService.getProdDtoByProdId(basketDto.getProdId());
            SkuDto skuDto = skuService.getSkuDtoBySkuId(basketDto.getSkuId());

            if (ObjUtil.isEmpty(prodDto) || ObjUtil.isEmpty(skuDto)) {
                basketDto.setStatus(SystemConstant.SYS_NO);
                // 商品不存在 直接返回
                if (ObjUtil.isEmpty(prodDto)) continue;
            }

            // 设置店铺名
            basketDto.setShopName(shopService.getShopDtoByShopId(prodDto.getShopId()).getShopName());

            basketDto.setProdDto(prodDto);
            basketDto.setSkuDto(skuDto);
        }

        return basketDtoList;
    }

    @Override
    public void sortListBySkuIdAsc(List<BasketDto> basketDtoList) {
        basketDtoList.sort(new Comparator<BasketDto>() {
            @Override
            public int compare(BasketDto o1, BasketDto o2) {
                return Long.valueOf(o1.getSkuId()).compareTo(Long.valueOf(o2.getSkuId()));
            }
        });
    }

}
