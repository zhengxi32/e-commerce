package com.xi.service;

import com.xi.domain.UserAddr;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户配送地址 服务类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-27
 */
public interface UserAddrService extends IService<UserAddr> {


    UserAddr getUserAddrByUserId(String userId, String addrId);
}
