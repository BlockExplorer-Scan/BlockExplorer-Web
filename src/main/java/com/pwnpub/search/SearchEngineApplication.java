package com.pwnpub.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages= {"com.pwnpub.search.web", "com.pwnpub.search.app", "com.pwnpub.search.config"})
@EnableAsync
public class SearchEngineApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(SearchEngineApplication.class);
    }


	public static void main(String[] args) {

		SpringApplication.run(SearchEngineApplication.class, args);

	}
}
