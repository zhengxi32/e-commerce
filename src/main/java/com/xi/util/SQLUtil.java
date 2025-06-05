package com.xi.util;

import cn.hutool.core.util.StrUtil;

public class SQLUtil {

    public static String formatOrderBy(String fieldList, String sortList) {
        if (StrUtil.isBlank(fieldList) || StrUtil.isBlank(sortList)) {
            return null;
        }
        String[] fields = fieldList.split(",");
        String[] sorts = sortList.split(",");
        if (fields.length != sorts.length) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            sb.append(fields[i]).append(" ").append(sorts[i]).append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }

}
