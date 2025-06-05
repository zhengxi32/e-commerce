package com.xi.entity.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProdParam {

    /**
     * 商品名称
     */
    @NotNull
    private String prodName;

    /**
     * 排序字段列表
     */
    private transient String fieldList;

    /**
     * 排序方法列表
     */
    private transient String sortList;

    /**
     * 排序条件
     */
    private transient String orderByStr;

    /**
     * 分页查询起始位置
     */
    private transient Integer page;

    /**
     * 分页查询每页大小
     */
    private transient Integer size;

}
