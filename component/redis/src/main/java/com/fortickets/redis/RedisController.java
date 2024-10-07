package com.fortickets.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {

    @Autowired
    private RedisService redisService;

    @GetMapping("/redis/save")
    public String saveToRedis(@RequestParam String key, @RequestParam String value) {
        redisService.saveToRedis(key, value);
        return "Saved to Redis";
    }

    @GetMapping("/redis/get")
    public String getFromRedis(@RequestParam String key) {
        String value = redisService.getFromRedis(key);
        return "Value from Redis: " + value;
    }
}
