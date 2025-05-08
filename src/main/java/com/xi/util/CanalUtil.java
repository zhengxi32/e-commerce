package com.xi.util;

import com.alibaba.otter.canal.protocol.CanalEntry;

import java.util.List;

public class CanalUtil {
    /**
     * 获取Integer类型字段
     * @param columns 列
     * @param name 字段名
     * @return 值
     */
    public static Integer getIntValue(List<CanalEntry.Column> columns, String name) {
        String val = getStringValue(columns, name);
        return val != null ? Integer.parseInt(val) : null;
    }

    /**
     * 获取Long类型字段
     * @param columns 列
     * @param name 字段名
     * @return 值
     */
    public static Long getLongValue(List<CanalEntry.Column> columns, String name) {
        String val = getStringValue(columns, name);
        return val != null ? Long.parseLong(val) : null;
    }

    /**
     * 获取String类型字段
     * @param columns 列
     * @param name 字段名
     * @return 值
     */
    public static String getStringValue(List<CanalEntry.Column> columns, String name) {
        return columns.stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .map(CanalEntry.Column::getValue)
                .orElse(null);
    }
}
