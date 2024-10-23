//package com.fortickets.gatewayservice.websocket;
//
//import java.util.HashMap;
//import java.util.Map;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.HandlerMapping;
//import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
//import org.springframework.web.reactive.socket.WebSocketHandler;
//import org.springframework.web.reactive.socket.server.WebSocketService;
//import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
//import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;
//import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
//import org.springframework.web.reactive.config.EnableWebFlux;
//import org.springframework.web.reactive.config.WebFluxConfigurer;
//
//@Configuration
//@EnableWebFlux
//public class WebSocketConfig implements WebFluxConfigurer {
//
//    @Bean
//    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
//        return new WebSocketHandlerAdapter(customWebSocketService());
//    }
//
//    @Bean
//    public WebSocketService customWebSocketService() {
//        return new HandshakeWebSocketService(new ReactorNettyRequestUpgradeStrategy());
//    }
//
//    @Bean
//    public HandlerMapping webSocketMapping(@Qualifier("customWebSocketHandler") WebSocketHandler webSocketHandler) {
//        Map<String, WebSocketHandler> map = new HashMap<>();
//        map.put("/ws/messages", webSocketHandler);
//        return new SimpleUrlHandlerMapping(map, 1);
//    }
//}
