package com.xi.service.consumer.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xi.domain.message.CanalMessage;
import com.xi.domain.message.ProdMessage;
import com.xi.service.consumer.ProdConsumerService;
import com.xi.util.JSONUtil;
import com.xi.util.LuaUtil;
import com.xi.util.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ProdConsumerServiceImpl implements ProdConsumerService {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "topic-prod", groupId = "daily-prod", concurrency = "1")
    public void listenStockChange(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            CanalMessage<ProdMessage> message = JSONUtil.fromJson(record.value(), new TypeReference<CanalMessage<ProdMessage>>() {});
            if ("tb_prod".equals(message.getTable())) {
                CompletableFuture.runAsync(() -> {
                    processProdChange(message);
                });
            }

            // 手动提交偏移量
            ack.acknowledge();

        } catch (Exception e) {
            log.error("消息消费失败，topic = {}, offset = {}", record.topic(), record.offset(), e);
        }

    }

    public void processProdChange(CanalMessage<ProdMessage> message) {
        message.getData().forEach(prod -> {
            switch (message.getType()) {
                case "INSERT" -> {

                    String versionKey = RedisUtil.getVersionKey(message.getTable(), prod.getProdId());
                    String stockKey = RedisUtil.getStockKey(message.getTable(), prod.getProdId());
                    LuaUtil.updateKeyValuePairs(redissonClient, message.getTable(), versionKey, prod.getVersion(), stockKey, prod.getTotalStocks());

                }
                case "UPDATE" -> {

                    // 旧版本号
                    String versionKey = RedisUtil.getVersionKey(message.getTable(), prod.getProdId());
                    String stockKey = RedisUtil.getStockKey(message.getTable(), prod.getProdId());
                    RBucket<Integer> rBucket = redissonClient.getBucket(versionKey);
                    Optional<Integer> oldVersion = Optional.of(rBucket.get());

                    // 执行更新
                    if (prod.getVersion() > oldVersion.get()) {
                        LuaUtil.updateKeyValuePairs(redissonClient, message.getTable(), versionKey, prod.getVersion(), stockKey, prod.getTotalStocks());
                    }

                }
                case "DELETE" -> {

                    String versionKey = RedisUtil.getVersionKey(message.getTable(), prod.getProdId());
                    String stockKey = RedisUtil.getStockKey(message.getTable(), prod.getProdId());

                    // 异步删除
                    RedisUtil.unlinkKeys(redissonClient, versionKey, stockKey);

                }
            }

            log.info("已同步产品 {} 变更到Redis: {}", prod.getProdId(), message.getType());
        });
    }

    @Override
    public void preDeduction(String prodId) {

        // 原子性扣减库存
        String stockKey = RedisUtil.getStockKey("tb_prod", prodId);
        redisTemplate.opsForValue().decrement(stockKey);

        // 发送消息 数据库库存扣减
    }
}
