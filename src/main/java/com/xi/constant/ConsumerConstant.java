package com.xi.constant;

public class ConsumerConstant {

    /**
     * 订单创建消费者组
     */
    public static final String ORDER_CREATE_GROUP = "ORDER_CREATE_GROUP";

    /**
     * 订单回滚消费者组
     */
    public static final String ORDER_ROLLBACK_GROUP = "ORDER_ROLLBACK_GROUP";

    /**
     * 订单取消消费者组
     */
    public static final String ORDER_CANCEL_GROUP = "ORDER_CANCEL_GROUP";

    /**
     * 库存扣减消费者组
     */
    public static final String STOCK_DECREASE_GROUP = "STOCK_DECREASE_GROUP";

    /**
     * 支付消费者组
     */
    public static final String PAYMENT_GROUP = "PAYMENT_GROUP";

    /**
     * 库存同步消费者组
     */
    public static final String CACHE_STOCK_SYNC_GROUP = "CACHE_STOCK_SYNC_GROUP";

    /**
     * 库存释放消费者组
     */
    public static final String STOCK_RELEASE_GROUP = "STOCK_RELEASE_GROUP";


}
