package com.xi.mapper;

import com.xi.domain.UserAddr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户配送地址 Mapper 接口
 * </p>
 *
 * @author 郑西
 * @since 2025-04-27
 */
@Mapper
public interface UserAddrMapper extends BaseMapper<UserAddr> {

    /**
     * 根据用户ID和地址ID获取地址
     * @param userId 用户ID
     * @param addrId 地址ID
     * @return 地址
     */
    UserAddr getUserAddrByUserIdAndAddrId(@Param("userId") String userId, @Param("addrId") String addrId);


}
