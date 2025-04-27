package com.xi.cache;

import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public class MultiLevelCacheManager implements CacheManager {

    private final CaffeineCacheManager caffeineCacheManager;

    private final RedisCacheManager redisCacheManager;

    private final Map<String, Cache> caches = new ConcurrentHashMap<>();

    @Override
    public Cache getCache(String name) {
        return caches.computeIfAbsent(name, cacheName ->
                new MultiLevelCache(
                        Objects.requireNonNull(caffeineCacheManager.getCache(cacheName)),
                        Objects.requireNonNull(redisCacheManager.getCache(cacheName))
                )
        );
    }

    @Override
    public Collection<String> getCacheNames() {
        return Stream.concat(
                caffeineCacheManager.getCacheNames().stream(),
                redisCacheManager.getCacheNames().stream()
        ).distinct().collect(Collectors.toList());
    }
}
