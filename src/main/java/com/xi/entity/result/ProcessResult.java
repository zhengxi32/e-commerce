package com.xi.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProcessResult {

    /**
     * 数据
     */
    private Object data;

    /**
     * 顺序消息分区键
     */
    private String routingKey;

}
