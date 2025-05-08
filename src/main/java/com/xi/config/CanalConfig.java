package com.xi.config;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import com.xi.entity.result.ProcessResult;
import com.xi.factory.TableProcessorRouterFactory;
import com.xi.processor.TableProcessor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@Import(CanalConfig.class)
public class CanalConfig {
    private static final Logger log = LoggerFactory.getLogger(CanalConfig.class);

    /**
     * Canal监听器
     * @param canalConnector Canal连接器
     * @param processorRouter 处理器路由工厂
     * @param rocketMQTemplate RocketMQ
     * @param internal 处理间隔
     * @param batchSize 批处理大小
     * @return 服务器启动时立即执行任务
     */
    @Bean
    @ConditionalOnProperty(name = "canal.enabled")
    public CommandLineRunner canalListener(CanalConnector canalConnector,
                                           TableProcessorRouterFactory processorRouter,
                                           RocketMQTemplate rocketMQTemplate,
                                           @Value("${canal.process.interval:100}") long internal,
                                           @Value("${canal.batch.size:1000}") int batchSize) {
        return args -> {
            log.info("以间隔 {} 秒与批处理大小 {} 启动Canal监听器", internal, batchSize);
            try {
                startProcessingLoop(canalConnector, processorRouter, rocketMQTemplate, internal, batchSize);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        };
    }

    private void startProcessingLoop(CanalConnector connector,
                                     TableProcessorRouterFactory router,
                                     RocketMQTemplate mqTemplate,
                                     long internal,
                                     int batchSize) {
        while (!Thread.currentThread().isInterrupted()) {
            Message message = connector.getWithoutAck(batchSize);
            long batchId = message.getId();

            try {
                // 合法
                if (batchId != -1) {
                    processBatch(message, router, mqTemplate);
                    // 确认
                    connector.ack(batchId);
                }
            } catch (Exception e) {
                log.error("{} 批处理失败", batchId, e);
                connector.rollback(batchId);
            }

            // 处理间隔
            sleepSafely(internal);
        }
    }

    /**
     * 批处理消息
     * @param message Canal原始消息
     * @param router 处理器路由工厂
     * @param mqTemplate RocketMQ
     */
    private void processBatch(Message message, TableProcessorRouterFactory router, RocketMQTemplate mqTemplate) {
        message.getEntries().stream()
                .collect(Collectors.groupingBy(entry -> entry.getHeader().getTableName()))
                .forEach((tableName, entries) -> {
                    try {
                        TableProcessor processor = router.route(tableName);
                        List<ProcessResult> results = processor.process(entries);

                        sendToRocketMQ(results, processor, router, mqTemplate);
                    } catch (Exception e) {
                        log.error("无法处理表 {} 数据，找不到适配处理器", tableName, e);
                    }
                });
    }

    /**
     * 发送消息至队列
     *
     * @param results    处理完成消息
     * @param processor  处理器
     * @param router     处理器路由工厂
     * @param mqTemplate RocketMQ
     */
    private void sendToRocketMQ(List<ProcessResult> results,
                                TableProcessor processor,
                                TableProcessorRouterFactory router,
                                RocketMQTemplate mqTemplate) {
        String topic = router.getTargetTopic(processor);
        results.forEach(result -> {
            mqTemplate.syncSendOrderly(
                    topic,
                    result.getData(),
                    result.getRoutingKey()
            );
        });
    }

    /**
     * 安全睡眠
     *
     * @param internal 睡眠时间
     */
    private void sleepSafely(long internal) {
        try {
            Thread.sleep(internal);
        } catch (InterruptedException e) {
            log.warn("Canal监听器睡眠期间被中断");
            e.printStackTrace();
        }
    }

}