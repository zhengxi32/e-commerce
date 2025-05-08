package com.xi.cache;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.lang.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@AllArgsConstructor
public class MultiLevelCache implements Cache {

    private final Cache caffeineCache;  // 一级缓存（本地）
    private final Cache redisCache;     // 二级缓存（分布式）
    private final String name;         // 缓存名称

    public MultiLevelCache(Cache caffeineCache, Cache redisCache) {
        this.caffeineCache = caffeineCache;
        this.redisCache = redisCache;
        this.name = caffeineCache.getName();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this;  // 返回当前缓存实例
    }

    /**
     * 核心方法：多级缓存查询（Caffeine → Redis）
     */
    @Override
    public ValueWrapper get(Object key) {
        // 1. 先查一级缓存
        ValueWrapper value = caffeineCache.get(key);
        if (value != null) {
            log.debug("[Caffeine] Hit the cache, key: {}", key);
            return value;
        }

        // 2. 查二级缓存
        value = redisCache.get(key);
        if (value != null) {
            log.debug("[Redis] Hit the cache key: {}", key);
            // 回填一级缓存
            caffeineCache.put(key, value.get());
            return value;
        }

        log.debug("Cache miss, key: {}", key);
        return null;
    }

    @Override
    @Nullable
    public <T> T get(Object key, @Nullable Class<T> type) {
        // 1. 先查一级缓存
        T value = caffeineCache.get(key, type);
        if (value != null) {
            return value;
        }

        // 2. 查二级缓存
        value = redisCache.get(key, type);
        if (value != null) {
            // 回填一级缓存
            caffeineCache.put(key, value);
            return value;
        }
        return null;
    }

    /**
     * 支持缓存加载器的多级查询
     */
    @Override
    @Nullable
    public <T> T get(Object key, Callable<T> valueLoader) {
        AtomicReference<T> result = new AtomicReference<>();
        try {
            // 双重检查锁定模式
            result.set(caffeineCache.get(key, () -> {
                T value = redisCache.get(key, valueLoader);
                if (value == null) {
                    value = valueLoader.call();  // 最终回源数据库
                }
                return value;
            }));
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
        return result.get();
    }

    /**
     * 写入缓存（同步两级）
     */
    @Override
    public void put(Object key, @Nullable Object value) {
        // 先写Redis保证集群一致性
        redisCache.put(key, value);
        // 再写Caffeine提升后续访问速度
        caffeineCache.put(key, value);
        log.debug("Cache writing, key: {}", key);
    }

    /**
     * 删除缓存（同步两级）
     */
    @Override
    public void evict(Object key) {
        // 先删Redis
        redisCache.evict(key);
        // 再删Caffeine
        caffeineCache.evict(key);
        log.debug("Delete cache key: {}", key);
    }

    /**
     * 清空缓存（同步两级）
     */
    @Override
    public void clear() {
        // 先清Redis
        redisCache.clear();
        // 再清Caffeine
        caffeineCache.clear();
        log.debug("Clear all caches");
    }

    /**
     * 原子性putIfAbsent操作
     */
    @Override
    @Nullable
    public ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        // Redis层面保证原子性
        ValueWrapper result = redisCache.putIfAbsent(key, value);
        if (result == null) {
            caffeineCache.put(key, value);  // 同步到本地
        }
        return result;
    }
}