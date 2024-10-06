//package computerdatabase;
//
//import static io.gatling.javaapi.core.CoreDsl.*;
//import static io.gatling.javaapi.http.HttpDsl.*;
//
//import io.gatling.javaapi.core.*;
//import io.gatling.javaapi.http.*;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//
//
//import java.security.Key;
//import java.util.Date;
//
//public class ScheduleSimulation1 extends Simulation {
//
//    // 환경 변수로부터 비밀키를 불러오기 (환경변수 이름: JWT_SECRET_KEY)
//    private static final String SECRET_KEY = System.getenv("JWT_SECRET_KEY");
//
//    // 환경변수에서 불러온 비밀키를 사용하여 Key 객체 생성 (키 길이 32바이트 이상)
//    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
//
//    // HTTP 프로토콜 설정
//    HttpProtocolBuilder httpProtocol = http
//            .baseUrl("http://localhost:12011")
//            .acceptHeader("application/json")
//            .contentTypeHeader("application/json")
//            .userAgentHeader("Gatling");
//
//    // JWT 토큰 동적으로 생성하는 메서드
//    public String generateJwtToken(String userId) {
//        return Jwts.builder()
//                .setSubject(userId)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1시간 유효
//                .signWith(key, SignatureAlgorithm.HS256) // 환경변수에서 불러온 키를 사용하여 서명
//                .compact();
//    }
//
//    // 시나리오 정의
//    ScenarioBuilder scn = scenario("스케줄 조회 시나리오")
//            // 1. JWT 토큰 동적 생성
//            .exec(session -> {
//                String randomUserId = "user" + (int) (Math.random() * 100); // 랜덤 유저 ID 생성
//                String jwtToken = generateJwtToken(randomUserId); // 유저 ID로 토큰 생성
//                return session.set("jwtToken", jwtToken); // 세션에 토큰 저장
//            })
//
//            // 2. 스케줄 조회 요청
//            .exec(http("스케줄 조회 요청")
//                    .get("/schedules/${scheduleId}") // 스케줄 조회 API
//                    .header("Authorization", "Bearer #{jwtToken}") // 동적으로 생성된 JWT 토큰 사용
//                    .check(status().is(200))
//                    .check(bodyString().saveAs("responseBody")) // 응답 본문 저장
//            )
//
//            // 3. 응답 내용 출력 (디버깅용)
//            .exec(session -> {
//                String responseBody = session.getString("responseBody");
//                System.out.println("Response Body: " + responseBody);
//                return session;
//            });
//
//    {
//        // 시뮬레이션 설정: 한 번에 100명의 사용자가 스케줄 조회 시도
//        setUp(scn.injectOpen(atOnceUsers(5))).protocols(httpProtocol);
//    }
//}
