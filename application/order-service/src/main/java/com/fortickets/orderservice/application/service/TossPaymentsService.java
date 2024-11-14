package com.fortickets.orderservice.application.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class TossPaymentsService {

    private final RestTemplate restTemplate;

    @Value("${toss.api-url}")
    private String apiUrl;

    @Value("${toss.secret-key}")
    private String secretKey;

    // 결제 요청
    public Map<String, Object> requestPayment(String paymentKey) {
        String encodedAuth = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        // Http Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(secretKey, ""); // 기본 인증 설정
        headers.set("Content-Type", "application/json");

        // Http Body 설정
        Map<String, Object> body = Map.of(
            "paymentKey", paymentKey
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // API 호출
        String requestUrl = apiUrl + "/v1/payments/confirm";
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            requestUrl,
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        return response.getBody();
    }

}
