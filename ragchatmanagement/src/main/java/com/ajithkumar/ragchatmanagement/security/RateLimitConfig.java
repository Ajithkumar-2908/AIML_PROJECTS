package com.ajithkumar.ragchatmanagement.security;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration class for rate limiting using Bucket4j with Redis.
 */
@Configuration
public class RateLimitConfig {

    @Value("${security.rate-limit.duration-minutes}")
    private String REQUEST_RATE_REFILL_MINUTES;

    @Bean
    public LettuceBasedProxyManager<byte[]> proxyManager(
            io.lettuce.core.RedisClient redisClient) {

        return LettuceBasedProxyManager.builderFor(redisClient)
                .withExpirationStrategy(
                        ExpirationAfterWriteStrategy
                                .basedOnTimeForRefillingBucketUpToMax(
                                        Duration.ofMinutes(Long.valueOf(REQUEST_RATE_REFILL_MINUTES))
                                )
                )
                .build();
    }
}


