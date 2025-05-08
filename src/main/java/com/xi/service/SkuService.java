package com.xi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xi.entity.dto.BasketDto;
import com.xi.entity.dto.SkuDto;
import com.xi.entity.param.OrderParam;
import com.xi.entity.tb.SkuDo;

import java.util.List;

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
    Boolean updateStocksLock(String skuId, Integer stocks, Integer skuVersion);

    /**
     * 单个下单场景
     *
     * @param orderParam 订单参数
     * @param skuVersion sku版本号
     * @return 更新结果
     */
    Boolean updateStocksLock(OrderParam orderParam, Integer skuVersion);

    /**
     * 购物车场景
     *
     * @param basketDto  购物车参数
     * @param skuVersion sku版本号
     * @return 更新结果
     */
    Boolean updateStocksLock(BasketDto basketDto, Integer skuVersion);

    /**
     * 根据SkuId查询库存与版本号信息
     *
     * @param skuId skuID
     */
    SkuDto getStocksAndVersionBySkuId(String skuId);

    /**
     * 获取所有有效的Sku库存与版本信息
     *
     * @return SKU
     */
    List<SkuDto> getStocksAndVersionAll();

    /**
     * 释放商品库存
     *
     * @param skuId  单品ID
     * @param stocks 库存数量
     */
    void releaseStock(String skuId, Integer stocks);
}
