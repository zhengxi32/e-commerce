package com.xi.util;

import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class LuaUtil {

    private static final Logger log = LoggerFactory.getLogger(LuaUtil.class);

    public static void updateKeyValuePairs(RedissonClient redissonClient, String table, String key1, Integer value1, String key2, Integer value2) {
        // Lua 脚本内容
        String script = "redis.call('SET', KEYS[1], ARGV[1]); " +
                "redis.call('SET', KEYS[2], ARGV[2]); " +
                "return 1;"; // 返回值，表示操作成功

        // 执行 Lua 脚本
        Long result = redissonClient.getScript().eval(
                RScript.Mode.READ_WRITE, // 执行模式
                script, // Lua 脚本内容
                RScript.ReturnType.INTEGER, // 返回类型
                Arrays.asList(key1, key2), // KEYS 参数
                value1, value2 // ARGV 参数
        );

        // 检查结果
        if (result != null && result == 1) {
            log.info("{} 表版本号与库存更新成功", table);
        } else {
            log.info("{} 表版本号与库存更新失败", table);
        }
    }

}
