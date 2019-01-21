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
 * @author soobeenwong
 * @date 2018-11-02 5:10 PM
 * @desc ES配置
 */
//@Configuration
public class EsConfig {

    @Bean
    public TransportClient client() throws UnknownHostException {

        EsConfig es = new EsConfig();

        Settings settings = Settings.builder().put("cluster.name", "EthDatas").build();

        TransportClient client = new PreBuiltTransportClient(settings);

        TransportAddress server1 = new TransportAddress(InetAddress.getByName("10.0.20.22"), 9300);
        TransportAddress server2 = new TransportAddress(InetAddress.getByName("10.0.20.142"), 8200);
        TransportAddress server3 = new TransportAddress(InetAddress.getByName("10.0.20.143"), 8000);

        client.addTransportAddress(server1);
        client.addTransportAddress(server2);
        client.addTransportAddress(server3);

        return client;

    }

}
