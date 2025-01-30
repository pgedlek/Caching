package com.pgedlek.client_spring_cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ClientSpringCacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientSpringCacheApplication.class, args);
    }

}
