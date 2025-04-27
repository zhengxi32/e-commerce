package com.xi.scheduler;

import com.xi.domain.Prod;
import com.xi.mapper.ProdMapper;
import com.xi.util.RedisUtil;
import jakarta.annotation.Resource;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DataConsistencyCheckTask {

    private static final Logger log = LoggerFactory.getLogger(DataConsistencyCheckTask.class);

    @Resource
    private ProdMapper prodMapper;

    @Resource
    private RedissonClient redissonClient;

    @Scheduled(fixedRate = 300000)
    public void checkStock() {
        List<Prod> items = prodMapper.selectList();
        items.forEach(item -> {
            String versionKey = RedisUtil.getVersionKey("tb_prod", item.getProdId());
            RBucket<Integer> bucket = redissonClient.getBucket(versionKey);
            Optional<Integer> version = Optional.ofNullable(bucket.get());
            if (version.get() != item.getVersion()) {
                log.warn("商品表数据不一致，prodId={}, redisVersion={}, dbVersion={}", item.getProdId(), version, item.getVersion());
            }
        });
    }

}
