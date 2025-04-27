package com.xi.service.consumer;

import com.xi.domain.message.CanalMessage;
import com.xi.domain.message.ProdMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;

public interface ProdConsumerService {

    /**
     * 库存变更监听器
     * @param record 变更数据
     * @param ack 手动提交偏移量
     */
    public void listenStockChange(ConsumerRecord<String, String> record, Acknowledgment ack);

    /**
     * 库存变更处理器
     * @param message 消息
     */
    public void processProdChange(CanalMessage<ProdMessage> message);

    /**
     * 预扣减
     */
    public void preDeduction(String prodId);

}
