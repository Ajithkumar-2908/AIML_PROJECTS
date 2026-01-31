package com.ajithkumar.ragchatmanagement.security;

import io.lettuce.core.RedisClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Bean
    public RedisClient redisClient() {
        // Use the Docker Compose service name 'redis'
        return RedisClient.create("redis://localhost:6379");
    }
}

