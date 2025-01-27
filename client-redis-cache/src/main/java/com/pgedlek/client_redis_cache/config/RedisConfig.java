package com.pgedlek.client_redis_cache.config;

import com.pgedlek.client_redis_cache.model.Profile;
import com.pgedlek.client_redis_cache.service.AppService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;

    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(host, port);

        return connectionFactory;
    }

    @Bean
    public ReactiveRedisTemplate<Long, Profile> reactiveRedisTemplate() {
        Jackson2JsonRedisSerializer<Profile> serializer = new Jackson2JsonRedisSerializer<>(Profile.class);

        RedisSerializationContext.RedisSerializationContextBuilder<Long, Profile> builder =
                RedisSerializationContext.newSerializationContext(serializer);

        RedisSerializationContext<Long, Profile> context = builder.build();

        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory(), context);
    }

    @Bean
    public AppService appService() {
        return new AppService(reactiveRedisTemplate());
    }
}
