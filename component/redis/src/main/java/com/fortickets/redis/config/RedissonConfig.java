package com.fortickets.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class RedissonConfig {
    @Value("${SPRING_REDIS_HOST:localhost}") // 기본값은 localhost
    private String redisHost;

    @Value("${SPRING_REDIS_PORT:6379}") // 기본값은 6379
    private int redisPort;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();


        // Redis 서버 주소와 비밀번호 설정 (필요에 따라 변경)
//        config.useSingleServer()
//                .setAddress("redis://localhost:6379") // Redis 서버 주소
//                .setAddress("redis://localhost:6380") // Redis 서버 주소
//                .setAddress("redis://localhost:6381") // Redis 서버 주소
//                .setAddress("redis://localhost:6382") // Redis 서버 주소
//                .setAddress("redis://localhost:6383") // Redis 서버 주소
//                .setAddress("redis://localhost:6384") // Redis 서버 주소
//                .setPassword("systempass"); // Redis 비밀번호

        config.useSingleServer()
            .setAddress("redis://" + redisHost + ":" + redisPort);

        return Redisson.create(config);
    }
}
