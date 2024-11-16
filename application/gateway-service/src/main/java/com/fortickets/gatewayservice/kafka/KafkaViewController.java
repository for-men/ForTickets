package com.fortickets.gatewayservice.kafka;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KafkaViewController {

    @GetMapping("/waiting-queue")
    public String mainPage() {
        return "index";
    }
}
