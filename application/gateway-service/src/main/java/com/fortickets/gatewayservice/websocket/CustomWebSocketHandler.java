//package com.fortickets.gatewayservice.websocket;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.socket.WebSocketHandler;
//import org.springframework.web.reactive.socket.WebSocketSession;
//import reactor.core.publisher.Mono;
//
//@Slf4j
//@Component("customWebSocketHandler")  // 빈 이름 명시
//public class CustomWebSocketHandler implements WebSocketHandler {
//
//    @Override
//    public Mono<Void> handle(WebSocketSession session) {
//        log.info("New WebSocket connection established: {}", session.getId());
//
//        // Echo 메시지 응답 처리
//        return session.send(
//            session.receive()
//                .doOnNext(message -> log.info("Received: {}", message.getPayloadAsText()))
//                .map(msg -> session.textMessage("Echo: " + msg.getPayloadAsText()))
//        );
//    }
//}
