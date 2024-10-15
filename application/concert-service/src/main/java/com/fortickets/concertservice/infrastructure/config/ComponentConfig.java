package com.fortickets.concertservice.infrastructure.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "com.fortickets.common.exception",
    "com.fortickets.common.jpa",
    "com.fortickets.common.security",
})
public class ComponentConfig {
}
