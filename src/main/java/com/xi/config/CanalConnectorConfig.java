package com.xi.config;

import com.alibaba.google.common.collect.Lists;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;

@Configuration
public class CanalConnectorConfig {

    @Bean(destroyMethod = "disconnect")
    public CanalConnector canalConnector(@Value("${canal.server}") String server,
                                         @Value("${canal.destination}") String destination,
                                         @Value("${canal.username}") String username,
                                         @Value("${canal.password}") String password,
                                         @Value("${canal.subscribe}") String filter) {
        List<InetSocketAddress> addresses = Collections.singletonList(
                new InetSocketAddress(server.split(":")[0], Integer.parseInt(server.split(":")[1]))
        );
        CanalConnector connector = CanalConnectors.newClusterConnector(addresses, destination, username, password);

        connector.connect();
        connector.subscribe(filter);
        return connector;
    }

}
