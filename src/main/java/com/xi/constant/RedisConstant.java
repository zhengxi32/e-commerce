package com.xi.constant;

public class RedisConstant {

    public static String getSkuKey(String skuId) {
        return "SKU:" + "STOCKS:" + skuId;
    }

    public static final String STOCKS = "STOCKS";

    public static final String VERSION = "VERSION";

}
