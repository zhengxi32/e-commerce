package com.xi.listener;

import com.xi.constant.ConsumerConstant;
import com.xi.constant.RedisConstant;
import com.xi.constant.TopicConstant;
import com.xi.entity.dto.OrderDto;
import com.xi.entity.message.SkuMessage;
import com.xi.entity.result.ProcessResult;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RocketMQMessageListener(
        topic = TopicConstant.SKU_CACHE_STOCK_SYNC,
        consumerGroup = ConsumerConstant.CACHE_STOCK_SYNC_GROUP
)
public class CacheStockSyncListener implements RocketMQListener<List<ProcessResult>> {

    @Resource
    private RedissonClient redissonClient;

    @Override
    public void onMessage(List<ProcessResult> results) {
        List<SkuMessage> skuMessageList = results.stream().map(ProcessResult::getData).map(data -> (SkuMessage) data).toList();
        for (SkuMessage skuMessage : skuMessageList) {
            RScript rScript = redissonClient.getScript();
            String luaScript = """
                    local version = redis.call('HGET', key[1], ARGV[1])
                    if tonumber(version) < tonumber(ARGV[2]) then
                        redis.call('HSET', key[1], ARGV[1], ARGV[2])
                        redis.call('HSET', key[1], ARGV[3], ARGV[4])
                        return 1
                    else
                        return 0
                    end
                    """;
            String sha1 = rScript.scriptLoad(luaScript);
            Integer re = rScript.evalSha(
                    RScript.Mode.READ_WRITE,
                    sha1,
                    RScript.ReturnType.INTEGER,
                    Collections.singletonList(RedisConstant.getSkuKey(skuMessage.getSkuId())),
                    RedisConstant.VERSION,
                    skuMessage.getAfterVersion(),
                    RedisConstant.STOCKS,
                    skuMessage.getAfterStocks()
            );
        }
    }
}
