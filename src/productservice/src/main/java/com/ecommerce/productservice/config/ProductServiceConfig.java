package com.ecommerce.productservice.config;

import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class ProductServiceConfig {

    private final RedissonClient redissonClient;

    public ProductServiceConfig(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Bean
    public CacheManager cacheManager() {
        Map<String, org.redisson.spring.cache.CacheConfig> config = new HashMap<>();

        // Configure "products" cache with a TTL (e.g., 15 minutes = 900000ms)
        config.put("products", new org.redisson.spring.cache.CacheConfig(900000, 0));
        // Configure "product" cache with a TTL (e.g., 1 hour = 3600000ms)
        config.put("product", new org.redisson.spring.cache.CacheConfig(3600000, 0));

        return new RedissonSpringCacheManager(redissonClient, config);
    }
}