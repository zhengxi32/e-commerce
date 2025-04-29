package com.xi.service;

import com.xi.domain.ProdDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xi.domain.dto.ProdDto;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-24
 */
public interface ProdService extends IService<ProdDo> {

    /**
     * 根据商品ID获取商品信息
     * @param prodId 商品ID
     * @return 商品信息
     */
    ProdDto getProdDtoByProdId(String prodId);

    /**
     * 扣减库存
     * @param prodId 商品ID
     * @param totalStocks 下单商品数
     */
    void updateStocks(String prodId, Integer totalStocks);

    /**
     * 扣减库存 乐观锁更新
     * @param prodId 商品ID
     * @param skuId skuID
     * @param totalStocks 扣减库存
     * @param prodVersion prod版本号
     * @param skuVersion sku版本号
     */
    public void updateStocksLock(String prodId, String skuId, Integer totalStocks, Integer prodVersion, Integer skuVersion);

    /**
     * 获取指定商品Sku
     * @param prodId 商品ID
     * @return 返回商品Sku库存与版本号
     */
    ProdDto getStocksAndVersion(String prodId);

}
