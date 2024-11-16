package com.fortickets.orderservice.application.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TossPaymentsService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${toss.api-url}")
    private String apiUrl;

    @Value("${toss.secret-key}")
    private String secretKey;

    // 결제 요청 메서드
    public JSONObject requestPayment(String paymentKey, String orderId, Long amount) throws JSONException {
        // 요청 데이터를 JSON으로 구성
        JSONObject requestData = new JSONObject();
        requestData.put("paymentKey", paymentKey);
        requestData.put("orderId", orderId);
        requestData.put("amount", amount);

        // 결제 확인 API 호출
        String url = apiUrl + "/v1/payments/confirm";
        JSONObject response;
        try {
            response = sendRequest(requestData, secretKey, url);
        } catch (IOException e) {
            logger.error("Error during payment confirmation request", e);
            response = new JSONObject();
            response.put("error", "Error during payment confirmation");
        }

        return response;
    }

    private JSONObject sendRequest(JSONObject requestData, String secretKey, String urlString) throws IOException, JSONException {
        HttpURLConnection connection = createConnection(secretKey, urlString);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestData.toString().getBytes(StandardCharsets.UTF_8));
        }

        try (InputStream responseStream = connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream();
            Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
            return (JSONObject) new JSONParser().parse(reader);
        } catch (Exception e) {
            logger.error("Error reading response", e);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Error reading response");
            return errorResponse;
        }
    }

    private HttpURLConnection createConnection(String secretKey, String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization",
            "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8)));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        return connection;
    }

}
