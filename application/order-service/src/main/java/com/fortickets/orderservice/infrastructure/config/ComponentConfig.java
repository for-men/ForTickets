package com.fortickets.orderservice.infrastructure.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "com.fortickets.exception",
    "com.fortickets.jpa",
    "com.fortickets.security"
})
public class ComponentConfig {
}

