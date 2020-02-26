package com.pwnpub.search.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @date 2018-12-13 5:19 PM
 * @desc 测试ES
 */
//@Configuration
public class LocalEsConfig {

    @Bean
    public TransportClient client() throws UnknownHostException {

        EsConfig es = new EsConfig();

        Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();

        TransportClient client = new PreBuiltTransportClient(settings);

        TransportAddress server1 = new TransportAddress(InetAddress.getByName("47.107.123.218"), 9300);

        client.addTransportAddress(server1);

        return client;

    }

}

