package com.xi.service.Impl;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xi.constant.RedisConstant;
import com.xi.convert.ProdConvert;
import com.xi.entity.param.ProdParam;
import com.xi.entity.tb.ProdDo;
import com.xi.entity.dto.ProdDto;
import com.xi.enums.ResponseCodeEnum;
import com.xi.exception.BizException;
import com.xi.mapper.ProdMapper;
import com.xi.service.ProdService;
import jakarta.annotation.Resource;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author 郑西
 * @since 2025-04-24
 */
@Service
public class ProdServiceImpl extends ServiceImpl<ProdMapper, ProdDo> implements ProdService, CommandLineRunner {

    @Value("${hot-spot.max-size}")
    private int maxSize;

    @Resource
    private RedissonClient redissonClient;

    @Override
    @Cacheable(value = "prod", key = "#prodCode")
    public ProdDto getProdDtoByProdCode(String prodCode) {
        if (redissonClient.getBucket(RedisConstant.checkProdExist(prodCode)).isExists()) {
            // 更新过期时间
            redissonClient.getBucket(RedisConstant.checkProdExist(prodCode)).set(true, 5, TimeUnit.MINUTES);
            throw new BizException(ResponseCodeEnum.PROD_NOT_EXIST);
        }
        ProdDo prodDo = this.baseMapper.getProdDtoByProdCode(prodCode);
        if (ObjUtil.isEmpty(prodDo)) {
            redissonClient.getBucket(RedisConstant.checkProdExist(prodCode)).set(true, 5, TimeUnit.MINUTES);
            throw new BizException(ResponseCodeEnum.PROD_NOT_EXIST);
        }
        return ProdConvert.INSTANCE.ProdDoToDto(prodDo);
    }

    /**
     * 热点商品初始化
     * @param args 参数
     */
    @Override
    public void run(String... args) {
        RScoredSortedSet<String> hotProducts = redissonClient.getScoredSortedSet(RedisConstant.HOT_PROD_KEY_SET);
        if (hotProducts.isExists()) {
            // 如果已存在则不重复初始化
            return;
        }
        refreshHotProdZSet(maxSize);
    }

    @Override
    public void refreshHotProdZSet(int refreshSize) {
        // 获取集合
        RScoredSortedSet<String> set = redissonClient.getScoredSortedSet(RedisConstant.HOT_PROD_KEY_SET);
        List<ProdDo> prodDoList = this.baseMapper.selectRandomList(refreshSize);

        // 更新集合
        prodDoList.forEach(prodDo -> {
            set.add(System.currentTimeMillis(), prodDo.getProdCode());
        });
    }

    @Override
    public IPage<ProdDto> generalSearch(ProdParam prodParam) {
        return null;
    }

}
