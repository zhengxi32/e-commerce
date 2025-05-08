package com.xi.config;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketMqConfig {

    @Bean
    public RocketMQTemplate rocketMQTemplate(@Value("${rocketmq.name-server}") String nameServer,
                                             @Value("${rocketmq.producer.group:}") String producerGroup,
                                             @Value("${rocketmq.producer.send-message-timeout:3000}") int timeout,
                                             @Value("${rocketmq.producer.retry-times-when-send-failed:2}") int retryTimesWhenSendFailed,
                                             @Value("${rocketmq.producer.retry-times-when-send-async-failed:2}") int retryTimesWhenSendAsyncFailed) {
        RocketMQTemplate template = new RocketMQTemplate();
        template.setProducer(new DefaultMQProducer());
        template.getProducer().setNamesrvAddr(nameServer);
        template.getProducer().setProducerGroup(producerGroup);
        template.getProducer().setSendMsgTimeout(timeout);
        template.getProducer().setRetryTimesWhenSendFailed(retryTimesWhenSendFailed);
        template.getProducer().setRetryTimesWhenSendAsyncFailed(retryTimesWhenSendAsyncFailed);
        return template;
    }

}
