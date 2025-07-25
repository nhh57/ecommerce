package com.ecommerce.inventoryservice.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
              .setAddress("redis://10.56.66.54:6319")
              .setDatabase(0); // Default Redis database

        // Use JsonJacksonCodec for serialization/deserialization
        // This requires Jackson dependencies, which Spring Boot usually includes
        config.setCodec(new JsonJacksonCodec());
        
        return Redisson.create(config);
    }
}