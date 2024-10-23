package com.fortickets.gatewayservice.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestData {

    private String method; // HTTP 메서드 추가
    private String url;
    private String headers;
    private String body;
    private String uuid;
}
