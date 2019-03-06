package com.pwnpub.search.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * @program storage-job
 * @author: joon.h
 * @create: 2019/02/21 15:47
 */
@Configuration
public class Web3jConfig {

    @Value("${web3j-address}")
    String web3j_address;

    @Bean
    public Web3j web3jConnector(){

        Web3j web3j = Web3j.build(new HttpService(web3j_address));

        return web3j;
    }
}
