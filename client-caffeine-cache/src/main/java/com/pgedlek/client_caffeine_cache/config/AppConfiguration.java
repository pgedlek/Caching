package com.pgedlek.client_caffeine_cache.config;

import com.pgedlek.client_caffeine_cache.service.AppService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {
    @Bean
    public AppService appService() {
        return new AppService();
    }
}
