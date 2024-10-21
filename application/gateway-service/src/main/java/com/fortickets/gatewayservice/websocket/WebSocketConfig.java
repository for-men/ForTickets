//package com.fortickets.gatewayservice.websocket;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.config.EnableWebFlux;
//import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//import org.springframework.web.reactive.socket.server.WebSocketConfigurer; // 추가된 임포트
//
//@Configuration
//@EnableWebFlux
//public class WebSocketConfig {
//
//    @Bean
//    public WebSocketHandlerAdapter handlerAdapter() {
//        return new WebSocketHandlerAdapter();
//    }
//
//    @Bean
//    public WebSocketHandlerImpl webSocketHandler() {
//        return new WebSocketHandlerImpl();
//    }
//
//    // WebSocket 핸들러 등록
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(webSocketHandler(), "/ws").setAllowedOrigins("*"); // 핸들러 등록
//    }
//}