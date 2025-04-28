package com.xi.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xi.domain.UserAddr;
import com.xi.mapper.UserAddrMapper;
import com.xi.service.UserAddrService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户配送地址 服务实现类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-27
 */
@Service
public class UserAddrServiceImpl extends ServiceImpl<UserAddrMapper, UserAddr> implements UserAddrService {

    @Override
    @Cacheable(cacheNames = "UserAddrDto", key = "#userId + ':' + #addrId")
    public UserAddr getUserAddrByUserId(String userId, String addrId) {
        return this.baseMapper.getUserAddrByUserIdAndAddrId(userId, addrId);
    }

}
