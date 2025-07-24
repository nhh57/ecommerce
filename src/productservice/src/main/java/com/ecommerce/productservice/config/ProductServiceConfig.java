package com.ecommerce.productservice.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class ProductServiceConfig {
    // RedisCacheManager will be auto-configured if spring-boot-starter-data-redis is on classpath
    // and Redis properties are configured in application.properties
}