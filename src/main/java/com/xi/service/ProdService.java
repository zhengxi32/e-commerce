package com.xi.service;

import com.xi.entity.tb.ProdDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xi.entity.dto.ProdDto;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-24
 */
public interface ProdService extends IService<ProdDo> {

    /**
     * 根据商品ID获取商品信息
     * @param prodId 商品ID
     * @return 商品信息
     */
    ProdDto getProdDtoByProdId(String prodId);

}
