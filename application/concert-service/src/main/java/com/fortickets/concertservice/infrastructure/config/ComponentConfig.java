package com.fortickets.concertservice.infrastructure.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "com.fortickets.exception",
    "com.fortickets.jpa"
})
public class ComponentConfig {
}

