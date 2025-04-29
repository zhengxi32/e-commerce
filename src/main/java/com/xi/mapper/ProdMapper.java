package com.xi.mapper;

import com.xi.domain.ProdDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xi.domain.dto.ProdDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author 郑西
 * @since 2025-04-24
 */
@Mapper
public interface ProdMapper extends BaseMapper<ProdDo> {

    List<ProdDo> selectList();

    Integer updateStocks(@Param("prodId") String prodId, @Param("totalStocks") Integer totalStocks);

    ProdDo getProdDtoByProdId(@Param("prodId") String prodId);

    Integer updateStocksLock(@Param("prodId") String prodId, @Param("totalStocks") Integer totalStocks, @Param("version") Integer version);

    ProdDo getTotalStocksAndVersionByProdId(@Param("prodId") String prodId);
}

