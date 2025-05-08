package com.xi.config;

import com.xi.entity.result.ProcessResult;
import com.xi.factory.TableProcessorRouterFactory;
import com.xi.processor.TableProcessor;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
public class CanalConfig {
    private static final Logger log = LoggerFactory.getLogger(CanalConfig.class);

    /**
     * Canal监听器主入口
     */
    @Bean
    @ConditionalOnProperty(name = "canal.enabled", havingValue = "true")
    public CommandLineRunner canalListener(
            CanalConnector canalConnector,
            TableProcessorRouterFactory processorRouter,
            RocketMQTemplate rocketMQTemplate,
            @Value("${canal.process.interval:100}") long processIntervalMs,
            @Value("${canal.batch.size:1000}") int batchSize) {

        return args -> {
            log.info("Starting Canal listener with interval {}ms and batch size {}",
                    processIntervalMs, batchSize);

            try {
                initCanalConnection(canalConnector);
                startProcessingLoop(canalConnector, processorRouter,
                        rocketMQTemplate,
                        processIntervalMs, batchSize);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info("Canal listener was interrupted");
            } catch (Exception e) {
                log.error("Canal listener terminated abnormally", e);
                throw e;
            } finally {
                disconnectQuietly(canalConnector);
            }
        };
    }

    private void initCanalConnection(CanalConnector connector) {
        connector.connect();
        connector.subscribe(".*\\..*");
        log.info("Connected to Canal server, subscribed to all tables");
    }

    private void startProcessingLoop(CanalConnector connector,
                                     TableProcessorRouterFactory router,
                                     RocketMQTemplate mqTemplate,
                                     long intervalMs,
                                     int batchSize) throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            Message message = connector.getWithoutAck(batchSize);
            long batchId = message.getId();

            try {
                if (isValidBatch(batchId)) {
                    processBatch(message, router, mqTemplate);
                    connector.ack(batchId);
                }
            } catch (Exception e) {
                log.error("Failed to process batch {}", batchId, e);
                connector.rollback(batchId);
            }

            sleepSafely(intervalMs);
        }
    }

    private void processBatch(Message message,
                              TableProcessorRouterFactory router,
                              RocketMQTemplate mqTemplate) {
        long startTime = System.nanoTime();
        int entryCount = message.getEntries().size();
        message.getEntries().stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getHeader().getTableName()))
                .forEach((tableName, entries) -> {
                    try {
                        TableProcessor processor = router.route(tableName);
                        List<ProcessResult> results = processor.process(entries);

                        sendToRocketMQ(results, processor, router, mqTemplate);
                    } catch (Exception e) {
                        log.error("Failed to process table {}", tableName, e);
                    }
                });
    }

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

    private boolean isValidBatch(long batchId) {
        return batchId != -1;
    }

    private void sleepSafely(long millis) throws InterruptedException {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.warn("Processing loop interrupted during sleep");
            throw e;
        }
    }

    private void disconnectQuietly(CanalConnector connector) {
        try {
            if (connector != null) {
                connector.disconnect();
                log.info("Canal connection disconnected");
            }
        } catch (Exception e) {
            log.warn("Error disconnecting Canal", e);
        }
    }
}