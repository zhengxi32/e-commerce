package com.xi.processor;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xi.entity.result.ProcessResult;

import java.util.List;

public interface TableProcessor {

    /**
     * 处理Canal条目
     */
    List<ProcessResult> process(List<CanalEntry.Entry> entries);

}
