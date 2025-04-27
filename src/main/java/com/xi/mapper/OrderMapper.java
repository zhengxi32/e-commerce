package com.xi.mapper;

import com.xi.domain.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author 郑西
 * @since 2025-04-24
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}
