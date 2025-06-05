package com.xi.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xi.entity.param.ProdParam;
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
     * 根据商品编码获取商品信息
     * @param prodCode 商品ID
     * @return 商品信息
     */
    ProdDto getProdDtoByProdCode(String prodCode);

    /**
     * 刷新热点商品
     * @param refreshSize 刷新大小
     */
    public void refreshHotProdZSet(int refreshSize);

    /**
     * 分页普通查询
     * @param prodParam 商品参数
     * @return
     */
     IPage<ProdDto> generalSearch(ProdParam prodParam);
}
