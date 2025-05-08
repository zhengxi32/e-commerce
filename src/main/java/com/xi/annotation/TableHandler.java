package com.xi.annotation;


import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface TableHandler {
    /**
     * 支持处理的表名（支持正则）
     */
    String[] tables();

    /**
     * 目标消息主题
     */
    String topic();

    /**
     * 顺序消息的分区键字段
     */
    String routingKey();
}