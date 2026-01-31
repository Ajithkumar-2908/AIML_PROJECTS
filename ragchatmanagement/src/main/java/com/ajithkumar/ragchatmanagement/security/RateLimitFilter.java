package com.ajithkumar.ragchatmanagement.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

    private final ProxyManager<byte[]> proxyManager;
    private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();  // Key: IP

    @Value("${security.rate-limit.capacity}")
    private String REQUEST_RATE_LIMIT;

    @Value("${security.rate-limit.duration-minutes}")
    private String REQUEST_RATE_DURATION_MINUTES;

    public RateLimitFilter(ProxyManager<byte[]> proxyManager) {
        this.proxyManager = proxyManager;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String ip = request.getRemoteAddr();

        // CRITICAL: Reuse the SAME bucket instance for this IP
        Bucket bucket = bucketCache.computeIfAbsent(ip, this::createBucketForIp);

//        Bucket bucket = proxyManager.builder()
//                .build(key, () -> BucketConfiguration.builder()
//                        .addLimit(Bandwidth.simple(20, Duration.ofMinutes(1)))
//                        .build());

        // To bypass OPTIONS call as Swagger might call this multiple times.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.getWriter().write("Too many requests");
        }
    }

    private Bucket createBucketForIp(String ip) {
        byte[] key = ("rate-limit:" + ip).getBytes(StandardCharsets.UTF_8);
        return proxyManager.builder()
                .build(key, () -> BucketConfiguration.builder()
                        .addLimit(Bandwidth.simple(Long.valueOf(REQUEST_RATE_LIMIT), Duration.ofMinutes(Long.valueOf(REQUEST_RATE_DURATION_MINUTES))))
                        .build());
    }
}


