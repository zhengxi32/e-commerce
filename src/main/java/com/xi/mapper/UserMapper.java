package com.xi.mapper;

import com.xi.entity.tb.UserDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author 郑西
 * @since 2025-04-27
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDo> {

}
