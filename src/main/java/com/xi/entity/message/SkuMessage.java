package com.xi.entity.message;

import lombok.Data;

@Data
public class SkuMessage {
    /**
     * 变更类型
     */
    private String entryType;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 单品ID
     */
    private String skuId;

    /**
     * 变更前库存
     */
    private Integer beforeStocks;

    /**
     * 变更后库存
     */
    private Integer afterStocks;

    /**
     * 变更前版本号
     */
    private Integer beforeVersion;

    /**
     * 变更后版本号
     */
    private Integer afterVersion;

    /**
     * 执行时间
     */
    private Long executeTime;
}
