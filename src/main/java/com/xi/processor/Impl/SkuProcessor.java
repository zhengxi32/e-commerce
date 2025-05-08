package com.xi.processor.Impl;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xi.annotation.TableHandler;
import com.xi.constant.RoutingConstant;
import com.xi.constant.TableConstant;
import com.xi.constant.TopicConstant;
import com.xi.entity.message.SkuMessage;
import com.xi.entity.result.ProcessResult;
import com.xi.enums.ResponseCodeEnum;
import com.xi.exception.BizException;
import com.xi.processor.TableProcessor;
import com.xi.util.CanalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@TableHandler(tables = {TableConstant.TB_SKU}, topic = TopicConstant.SKU_CACHE_STOCK_SYNC, routingKey = RoutingConstant.SKU_ROUTING_KEY)
public class SkuProcessor implements TableProcessor {

    @Override
    public List<ProcessResult> process(List<CanalEntry.Entry> entries) {
        return entries.stream()
                .filter(entry -> entry.getEntryType() == CanalEntry.EntryType.ROWDATA)
                .map(this::parseRowChange)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(this::convertToProcessResult)
                .collect(Collectors.toList());
    }

    private List<SkuMessage> parseRowChange(CanalEntry.Entry entry) {
        try {
            CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            return rowChange.getRowDatasList().stream()
                    .map(rowData -> convertRowData(entry, rowChange, rowData))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("{} Table change resolution failed:", entry.getHeader().getTableName(), e);
            throw new BizException(ResponseCodeEnum.CANAL_ANALYSIS_FAILURE);
        }
    }

    private SkuMessage convertRowData(CanalEntry.Entry entry,
                                        CanalEntry.RowChange rowChange,
                                        CanalEntry.RowData rowData) {
        SkuMessage message = new SkuMessage();
        message.setEventType(rowChange.getEventType().name());
        message.setTableName(entry.getHeader().getTableName());
        message.setExecuteTime(entry.getHeader().getExecuteTime());

        List<CanalEntry.Column> beforeColumns = rowData.getBeforeColumnsList();
        List<CanalEntry.Column> afterColumns = rowData.getAfterColumnsList();


        message.setSkuId(CanalUtil.getStringValue(afterColumns, "sku_id"));
        message.setBeforeStocks(CanalUtil.getIntValue(beforeColumns, "stock"));
        message.setAfterStocks(CanalUtil.getIntValue(afterColumns, "stock"));
        message.setBeforeVersion(CanalUtil.getIntValue(beforeColumns, "version"));
        message.setAfterVersion(CanalUtil.getIntValue(afterColumns, "version"));

        return message;
    }

    private ProcessResult convertToProcessResult(SkuMessage message) {
        return new ProcessResult(message, message.getSkuId());
    }
}
