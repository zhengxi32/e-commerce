package com.xi.mapper;

import com.xi.entity.tb.SkuDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xi.entity.dto.SkuDto;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品规格表 Mapper 接口
 * </p>
 *
 * @author 郑西
 * @since 2025-04-27
 */
@Mapper
public interface SkuMapper extends BaseMapper<SkuDo> {

    Integer updateStocks(@Param("skuId") String skuId, @Param("stocks") Integer stocks);

    SkuDo getSkuDoBySkuId(@Param("skuId") String skuId);

    SkuDo updateStocksLock(@Param("skuId") String skuId, @Param("stocks") Integer stocks, @Param("version") Integer version);

    @MapKey("skuId")
    Map<String, SkuDto> getStocksAndVersionByProdId(String prodId);

    List<String> getSkuIdsByProdId(String prodId);

    SkuDo getStocksAndVersionBySkuId(String skuId);

    List<SkuDo> getStocksAndVersionAll();

    Integer releaseStock(@Param("skuId") String skuId, @Param("stocks") Integer stocks, @Param("version") Integer version);
}
