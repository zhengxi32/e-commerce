package com.xi.mapper;

import com.xi.domain.SkuDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xi.domain.dto.SkuDto;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    Integer updateStocksLock(@Param("skuId") String skuId, @Param("stocks") Integer stocks, @Param("version") Integer version);

    @MapKey("skuId")
    Map<String, SkuDto> getStocksAndVersionByProdId(String prodId);

    List<String> getSkuIdsByProdId(String prodId);

    SkuDo getStocksAndVersionBySkuId(String skuId);
}
