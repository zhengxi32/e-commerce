package com.xi.mapper;

import com.xi.domain.OrderDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author 郑西
 * @since 2025-04-29
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderDo> {

}
