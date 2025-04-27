package com.xi.service;

import com.xi.domain.Prod;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-24
 */
public interface ProdService extends IService<Prod> {
    void updateStock(String prodId);
}
