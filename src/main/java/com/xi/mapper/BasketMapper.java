package com.xi.mapper;

import com.xi.entity.tb.BasketDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xi.entity.dto.BasketDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 购物车 Mapper 接口
 * </p>
 *
 * @author 郑西
 * @since 2025-04-27
 */
@Mapper
public interface BasketMapper extends BaseMapper<BasketDo> {

    List<BasketDto> getBasketListByUserId(String userId);

}
