package com.xi.mapper;

import com.xi.domain.Sku;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 商品规格表 Mapper 接口
 * </p>
 *
 * @author 郑西
 * @since 2025-04-27
 */
@Mapper
public interface SkuMapper extends BaseMapper<Sku> {

    Integer updateStocks(String skuId, String stocks);

}
