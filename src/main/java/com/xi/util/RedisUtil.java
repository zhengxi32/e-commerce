package com.xi.util;

import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisUtil {

    private static final Logger log = LoggerFactory.getLogger(RedisUtil.class);

    private static final String VERSION_KEY = "version";

    private static final String SEPARATOR = ":";

    private static final String STOCK_KEY = "stock";

    public static String getVersionKey(String table, String key) {
        return table + SEPARATOR + key + SEPARATOR + VERSION_KEY;
    }

    public static String getStockKey(String table, String key) {
        return table + SEPARATOR + key + SEPARATOR + STOCK_KEY;
    }

    public static void unlinkKeys(RedissonClient redissonClient, String... key) {
        RKeys keys = redissonClient.getKeys();
        keys.unlink(key);
    }

}
