package com.xi.factory;

import com.xi.annotation.TableHandler;
import com.xi.processor.TableProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TableProcessorRouterFactory {
    private final Map<String, TableProcessor> processorMap = new ConcurrentHashMap<>();
    private final Map<TableProcessor, TableHandler> annotationCache = new ConcurrentHashMap<>();

    @Autowired
    public TableProcessorRouterFactory(List<TableProcessor> processors) {
        // 初始化处理器映射
        processors.forEach(processor -> {
            TableHandler annotation = processor.getClass().getAnnotation(TableHandler.class);
            if (annotation != null) {
                annotationCache.put(processor, annotation);
                Arrays.stream(annotation.tables())
                        .forEach(table -> processorMap.put(table.toLowerCase(), processor));
            }
        });
    }

    public TableProcessor route(String tableName) {
        TableProcessor processor = processorMap.get(tableName.toLowerCase());
        if (processor == null) {
            // 尝试正则匹配
            processor = processorMap.entrySet().stream()
                    .filter(e -> tableName.matches(e.getKey()))
                    .findFirst()
                    .map(Map.Entry::getValue)
                    .orElseThrow(() -> new IllegalArgumentException("没有合适处理器: " + tableName));
        }
        return processor;
    }

    public String getTargetTopic(TableProcessor processor) {
        return annotationCache.get(processor).topic();
    }

    public String getRoutingKeyField(TableProcessor processor) {
        return annotationCache.get(processor).routingKey();
    }
}
