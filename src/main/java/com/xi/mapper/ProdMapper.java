package com.xi.mapper;

import com.xi.domain.Prod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
public interface ProdMapper extends BaseMapper<Prod> {

    List<Prod> selectList();

    Integer updateStocks(@Param("prodId") String prodId, @Param("totalStocks") String totalStocks);

}

