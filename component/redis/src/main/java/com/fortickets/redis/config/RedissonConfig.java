package com.fortickets.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class RedissonConfig {

    private static final Logger log = LoggerFactory.getLogger(RedissonConfig.class);
    @Value("${SPRING_REDIS_HOST:localhost}") // 기본값은 localhost
    private String redisHost;

    @Value("${SPRING_REDIS_PORT:6379}") // 기본값은 6379
    private int redisPort;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        log.info("RedissonConfig redisHost: " + redisHost);
        log.info("RedissonConfig redisPort: " + redisPort);

        config.useSingleServer()
            .setAddress("redis://" + redisHost + ":" + redisPort);

        return Redisson.create(config);
    }
}
