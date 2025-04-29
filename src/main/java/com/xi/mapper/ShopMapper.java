package com.xi.mapper;

import com.xi.domain.ShopDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 店铺表 Mapper 接口
 * </p>
 *
 * @author 郑西
 * @since 2025-04-27
 */
@Mapper
public interface ShopMapper extends BaseMapper<ShopDo> {

    ShopDo getShopDtoByShopId(String shopId);
}
