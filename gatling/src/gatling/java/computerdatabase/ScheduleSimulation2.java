//package computerdatabase;
//
//import io.gatling.javaapi.core.ScenarioBuilder;
//import io.gatling.javaapi.core.Simulation;
//import io.gatling.javaapi.http.HttpProtocolBuilder;
//
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//import java.util.Base64;
//import java.util.Date;
//import java.util.concurrent.ThreadLocalRandom;
//
//import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
//import static io.gatling.javaapi.core.CoreDsl.bodyString;
//import static io.gatling.javaapi.core.CoreDsl.scenario;
//import static io.gatling.javaapi.http.HttpDsl.http;
//import static io.gatling.javaapi.http.HttpDsl.status;
//
//public class ScheduleSimulation2 extends Simulation {
//
//    // 시크릿 키 (Base64로 인코딩된 문자열)
//    private static final String SECRET_KEY = "401b09eab3c013d4ca54922bb802bec8fd5318192b0a75f201d8b3727429080fb337591abd3e44453b954555b7a0812e1081c39b740293f765eae731f5a65ed1";
//
//    // JWT 페이로드 키
//    private static final String USER_ID_KEY = "userId";
//    private static final String USER_EMAIL_KEY = "email";
//    private static final String USER_ROLE_KEY = "role";
//
//    // HTTP 프로토콜 설정
//    HttpProtocolBuilder httpProtocol = http
//            .baseUrl("http://localhost:12011") // API의 기본 URL
//            .acceptHeader("application/json")
//            .contentTypeHeader("application/json")
//            .userAgentHeader("Gatling");
//
//    // JWT 토큰 동적으로 생성하는 메서드
//    public String generateJwtToken(Long userId, String email, String role) {
//        long nowMillis = System.currentTimeMillis();
//        long expMillis = nowMillis + 3600000; // 1시간 유효
//        Date now = new Date(nowMillis);
//        Date exp = new Date(expMillis);
//
//        // JWT 헤더
//        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
//
//        // JWT 페이로드 (Claim 수정)
//        String payload = String.format("{\"%s\":%d,\"%s\":\"%s\",\"%s\":\"%s\",\"iat\":%d,\"exp\":%d}",
//                USER_ID_KEY, userId, USER_EMAIL_KEY, email, USER_ROLE_KEY, role, nowMillis / 1000, expMillis / 1000);
//
//        // Base64Url 인코딩
//        String encodedHeader = Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes());
//        String encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes());
//
//        // 서명 생성
//        try {
//            Mac hmac = Mac.getInstance("HmacSHA256");
//            SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
//            hmac.init(secretKeySpec);
//            byte[] signatureBytes = hmac.doFinal((encodedHeader + "." + encodedPayload).getBytes());
//            String signature = Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
//
//            // JWT 토큰 반환
//            return encodedHeader + "." + encodedPayload + "." + signature;
//        } catch (Exception e) {
//            throw new RuntimeException("JWT 토큰 생성 중 오류 발생", e);
//        }
//    }
//
//    // 시나리오 정의
//    ScenarioBuilder scn = scenario("스케줄 조회 시나리오")
//            // 1. 데이터 생성
//            .exec(session -> {
////                Long randomUserId = (long) ThreadLocalRandom.current().nextInt(1, 101); // 랜덤 유저 ID 생성
//                Long randomUserId = 2L;
////                String email = "user" + randomUserId + "@example.com"; // 랜덤 이메일 생성
//                String email = "user1@email.com";
//                String role = "ROLE_USER"; //
//                String jwtToken = generateJwtToken(randomUserId, email, role); // 유저 ID, 이메일, 역할로 토큰 생성
////                Long scheduleId = (long) ThreadLocalRandom.current().nextInt(1, 5); // 랜덤 Long 타입 스케줄 ID 생성
//                Long scheduleId = 1L;
//                System.out.println("Generated userId: " + randomUserId); // 추가된 로그
//                System.out.println("Generated scheduleId: " + scheduleId); // 추가된 로그
//                System.out.println("Generated jwtToken: " + jwtToken); // 추가된 로그
//                return session.set("jwtToken", jwtToken).set("scheduleId", scheduleId); // 세션에 토큰과 스케줄 ID 저장
////                return session.set("scheduleId", scheduleId); // 세션에 토큰과 스케줄 ID 저장
//            })
//
//            // 2. 스케줄 조회 요청
//            .exec(http("스케줄 조회 요청")
//                    .get("/concert-service/schedules/#{scheduleId}") // 스케줄 조회 API
////                    .header("Authorization", "Bearer #{jwtToken}") // 동적으로 생성된 JWT 토큰 사용
//                    .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjIsImVtYWlsIjoidXNlcjFAZW1haWwuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlzcyI6InVzZXItc2VydmljZSIsImlhdCI6MTcyODIxMDMxOSwiZXhwIjoxNzI4MjEzOTE5fQ.MY6_7hFHDmph1mtaVVT1uZKchho_y5ZACmyDiCjnyew") // 동적으로 생성된 JWT 토큰 사용
//                    .check(status().is(200)) // 응답 상태 코드 체크
//                    .check(bodyString().saveAs("responseBody")) // 응답 본문 저장
//            )
//
//            // 3. 응답 내용 출력
//            .exec(session -> {
//                String jwtToken = session.getString("jwtToken");
//                Long scheduleId = session.getLong("scheduleId");
//                System.out.println("Requesting with JWT Token: " + jwtToken); // 요청할 JWT 토큰 로그
//                System.out.println("Requesting scheduleId: " + scheduleId); // 요청할 scheduleId 로그
//                return session;
//            })
//            .exec(session -> {
//                String responseBody = session.getString("responseBody");
//                System.out.println("Response Body: " + responseBody); // 응답 본문 출력
//                return session;
//            });
//
//    {
//        // 시뮬레이션 설정: 한 번에 n명의 사용자가 스케줄 조회 시도
//        setUp(scn.injectOpen(atOnceUsers(100))).protocols(httpProtocol);
//    }
//}
