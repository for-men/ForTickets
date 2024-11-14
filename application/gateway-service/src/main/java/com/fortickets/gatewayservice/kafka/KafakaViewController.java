package com.fortickets.gatewayservice.kafka;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KafakaViewController {

    @GetMapping("/waiting-queue")
    public String mainpage() {
        return "index";
    }
}
