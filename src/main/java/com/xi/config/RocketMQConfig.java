package com.xi.config;

import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketMQConfig {

    @Resource
    private RocketMQProperties properties;

    @Bean
    public RocketMQTemplate rocketMQTemplate(){
        RocketMQTemplate template = new RocketMQTemplate();
        template.getProducer().setNamesrvAddr(properties.getNameServer());
        template.getProducer().setProducerGroup(properties.getProducer().getGroup());
        template.getProducer().setSendMsgTimeout(properties.getProducer().getSendMessageTimeout());
        return template;
    }

}
