package com.xi.service;

import com.xi.domain.BasketDo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xi.domain.OrderDo;
import com.xi.domain.dto.BasketDto;
import com.xi.domain.dto.OrderDto;

import java.util.List;

/**
 * <p>
 * 购物车 服务类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-27
 */
public interface BasketService extends IService<BasketDo> {

    /**
     * 根据用户ID获取购物车列表
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<BasketDto> getBasketListByUserId(String userId);

    /**
     * 根据skuId升序排序
     * @param basketDtoList 购物车列表
     */
    void sortListBySkuIdAsc(List<BasketDto> basketDtoList);

}
