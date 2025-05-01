package com.xi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xi.domain.SkuDo;
import com.xi.domain.dto.SkuDto;

/**
 * <p>
 * 商品规格表 服务类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-27
 */
public interface SkuService extends IService<SkuDo> {

    /**
     * 根据SkuID查询Sku信息
     *
     * @param skuId SkuID
     * @return Sku
     */
    SkuDto getSkuDtoBySkuId(String skuId);

    /**
     * 乐观锁更新库存
     *
     * @param skuId      skuID
     * @param stocks     扣减库存数量
     * @param skuVersion 版本号
     */
    void updateStocksLock(String skuId, Integer stocks, Integer skuVersion);

    /**
     * 根据SkuId查询库存与版本号信息
     *
     * @param skuId skuID
     */
    SkuDto getStocksAndVersionBySkuId(String skuId);

}
