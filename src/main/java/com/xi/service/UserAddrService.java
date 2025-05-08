package com.xi.service;

import com.xi.entity.tb.UserAddrDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xi.entity.dto.UserAddrDto;

/**
 * <p>
 * 用户配送地址 服务类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-27
 */
public interface UserAddrService extends IService<UserAddrDo> {

    /**
     * 根据用户ID和地址ID查询地址
     * @param userId 用户ID
     * @param addrId 地址ID
     * @return 地址
     */
    UserAddrDto getUserAddrDtoByUserIdAndAddrId(String userId, String addrId);

    /**
     * 查询默认地址 无默认地址则返回最后使用地址
     * @param userId 用户ID
     * @return 地址
     */
    UserAddrDto getCommonAddr(String userId);

}
