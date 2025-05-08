package com.xi.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
