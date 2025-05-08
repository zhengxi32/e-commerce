package com.xi.annotation;

import com.xi.constant.TopicConstant;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StockDecreaseResult {
    String paymentTopic() default TopicConstant.PAYMENT_TOPIC;

    String skuCacheStockSync() default TopicConstant.SKU_CACHE_STOCK_SYNC;

    String orderRollbackTopic() default TopicConstant.ORDER_ROLLBACK_TOPIC;
}
