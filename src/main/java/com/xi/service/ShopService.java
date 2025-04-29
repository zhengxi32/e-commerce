package com.xi.service;

import com.xi.domain.ShopDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xi.domain.dto.ShopDto;

/**
 * <p>
 * 店铺表 服务类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-27
 */
public interface ShopService extends IService<ShopDo> {

    /**
     * 根据店铺ID获取店铺信息
     * @param shopId 店铺ID
     * @return 店铺信息
     */
    ShopDto getShopDtoByShopId(String shopId);

}
