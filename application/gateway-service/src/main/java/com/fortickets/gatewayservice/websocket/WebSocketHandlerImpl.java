//package com.fortickets.gatewayservice.websocket;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.socket.WebSocketSession;
//import org.springframework.web.reactive.socket.WebSocketHandler; // 필요한 임포트 추가
//import reactor.core.publisher.Mono;
//import reactor.core.publisher.Flux; // 필요한 임포트 추가
//
//@Component
//public class WebSocketHandlerImpl implements WebSocketHandler {
//
//    @Override
//    public Mono<Void> handle(WebSocketSession session) {
//        return session.send(
//            session.receive()
//                .map(msg -> {
//                    String response = "Echo: " + msg.getPayloadAsText();
//                    return session.textMessage(response);
//                })
//        );
//    }
//}
