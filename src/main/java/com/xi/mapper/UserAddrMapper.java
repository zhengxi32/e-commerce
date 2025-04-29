package com.xi.mapper;

import com.xi.domain.UserAddrDo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xi.domain.dto.UserAddrDto;
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
public interface UserAddrMapper extends BaseMapper<UserAddrDo> {

    /**
     * 根据用户ID和地址ID获取地址
     * @param userId 用户ID
     * @param addrId 地址ID
     * @return 地址
     */
    UserAddrDo getUserAddrByUserIdAndAddrId(@Param("userId") String userId, @Param("addrId") String addrId);


    UserAddrDto getCommonAddr(String userId);

    UserAddrDto getRecentAddr(String userId);
}
