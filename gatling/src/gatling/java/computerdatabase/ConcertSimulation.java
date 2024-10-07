package computerdatabase;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.bodyString;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class ConcertSimulation extends Simulation {

    // HTTP 프로토콜 설정
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:12011") // API의 기본 URL, 환경에 맞게 설정
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .userAgentHeader("Gatling");

    // 시나리오 정의
    ScenarioBuilder scn = scenario("Concert 조회 시나리오")
            // 1. 데이터 생성
            .exec(session -> {
                // JWT 토큰을 포스트맨에서 복사하여 입력
                String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjIsImVtYWlsIjoidXNlcjFAZW1haWwuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlzcyI6InVzZXItc2VydmljZSIsImlhdCI6MTcyODI3ODQ5MywiZXhwIjoxNzI4MjgyMDkzfQ.DG-1wibnFgFwh5Pg63dEXJFQdOF8JZu2I7QVWYeNHzo"; // 포스트맨에서 받은 JWT 토큰
                Long concertId = 1L; // 고정된 콘서트 ID
                return session.set("jwtToken", jwtToken).set("concertId", concertId); // 세션에 토큰과 콘서트 ID 저장
            })

            // 2. Concert 조회 요청
            .exec(http("Concert 조회 요청")
                    .get("/concert-service/concerts/#{concertId}") // 콘서트 조회 API
                    .header("Authorization", "Bearer #{jwtToken}") // 동적으로 생성된 JWT 토큰 사용
                    .check(status().is(200)) // 응답 상태 코드 체크
                    .check(bodyString().saveAs("responseBody")) // 응답 본문 저장
            );

    {
        // 시뮬레이션 설정: 한 번에 5000명의 사용자가 콘서트 조회 시도
        setUp(scn.injectOpen(atOnceUsers(1000))).protocols(httpProtocol);
    }
}
