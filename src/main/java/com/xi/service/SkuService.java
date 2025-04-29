package com.xi.service;

import com.xi.domain.SkuDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xi.domain.dto.SkuDto;

import java.util.List;
import java.util.Map;

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
     * @param skuId SkuID
     * @return Sku
     */
    SkuDto getSkuDtoBySkuId(String skuId);

    /**
     * 更新库存
     * @param skuId skuID
     * @param stocks 扣减库存量
     */
    void updateStocks(String skuId, Integer stocks);

    /**
     * 乐观锁更新库存
     * @param skuId skuID
     * @param stocks 扣减库存数量
     * @param skuVersion 版本号
     * @return 更新结果
     */
    Integer updateStocksLock(String skuId, Integer stocks, Integer skuVersion);

    /**
     * 获取对应产品的SKU库存与版本号
     * @param prodId 产品ID
     * @return SKU库存与版本号
     */
    Map<String, SkuDto> getStocksAndVersionByProdId(String prodId);
}
