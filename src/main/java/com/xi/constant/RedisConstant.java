package com.xi.constant;

public class RedisConstant {

    /**
     * SKU 哈希类型 键
     * @param skuId 单品ID
     * @return 键
     */
    public static String getSkuKey(String skuId) {
        return "SKU:" + skuId;
    }

    /**
     * Sku 哈希类型 库存数据键
     */
    public static final String STOCKS = "STOCKS";

    /**
     * Sku 哈希类型 版本号键
     */
    public static final String VERSION = "VERSION";

    /**
     * 订单创建消息队列键
     * @param orderSerialNumber 订单流水号
     * @return 键
     */
    public static String getOrderCreateMessageKey(String orderSerialNumber) {return "ORDER:" + "CREATE:" + orderSerialNumber;}

    /**
     * 库存处理消息队列键
     * @param orderSerialNumber 订单流水号
     * @return 键
     */
    public static String getStocksDecreaseMessageKey(String orderSerialNumber) {return "STOCK:" + "DECREASE:" + orderSerialNumber;}

    /**
     * 库存释放消息队列键
     * @param orderSerialNumber 订单流水号
     * @return 键
     */
    public static String getStocksReleaseMessageKey(String orderSerialNumber) {return "STOCK:" + "RELEASE:" + orderSerialNumber;}

    /**
     * 解决缓存穿透
     */
    public static String checkProdExist(String prodCode) {return "PROD:" + "EXISTS:" + prodCode;}

    /**
     * 热点商品集合
     */
    public static final String HOT_PROD_KEY_SET = "HOT:PROD:KEY:SET";

    /**
     * 有效商品集合
     */
    public static final String VALID_PROD_KEY_SET = "VALID:PROD:KEY:SET";

    /**
     * sku单品锁
     */
    public static final String SKU_LOCK = "SKU:LOCK:";

}
