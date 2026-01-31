package com.ajithkumar.ragchatmanagement.security;

import io.lettuce.core.RedisClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String REDIS_HOST;

    @Value("${spring.data.redis.port}")
    private String REDIS_PORT;

    @Bean
    public RedisClient redisClient() {
        // Use the Docker Compose service name 'redis'
        return RedisClient.create("redis://" + REDIS_HOST + ":" + REDIS_PORT);
    }
}

