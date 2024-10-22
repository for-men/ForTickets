package computerdatabase;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.util.concurrent.ThreadLocalRandom;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.bodyString;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
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

    // 랜덤 공연 아이디 생성
    private Long getRandomConcertId() {
        return (long) ThreadLocalRandom.current().nextInt(1, 21); // 1부터 10까지의 랜덤 Long 값 생성
    }

    // 시나리오 정의
    ScenarioBuilder scn = scenario("Concert 조회 시나리오")
            // 1. 데이터 생성
            .exec(session -> {
                // JWT 토큰을 포스트맨에서 복사하여 입력
                String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjUsImVtYWlsIjoibWFuYWdlcjNAZW1haWwuY29tIiwicm9sZSI6IlJPTEVfTUFOQUdFUiIsImlzcyI6InVzZXItc2VydmljZSIsImlhdCI6MTcyOTQzMDAzOCwiZXhwIjoxNzI5NDMzNjM4fQ.jSYZW26tbCJQX18zLDdjt-QhtJOuytUZxZrTxWchI54"; // 포스트맨에서 받은 JWT 토큰
                Long concertId = getRandomConcertId(); // 랜덤한 스케줄 ID 생성
                return session.set("jwtToken", jwtToken).set("concertId", concertId); // 세션에 토큰과 콘서트 ID 저장
            })

            // 2. Concert 조회 요청
            .exec(http("Concert 조회 요청")
                    .get("/concert-service/concerts/#{concertId}") // 콘서트 조회 API
                    .header("Authorization", "Bearer #{jwtToken}") // 동적으로 생성된 JWT 토큰 사용
                    .check(status().in(200, 202)) // 응답 상태 코드 체크
                    .check(bodyString().saveAs("responseBody")) // 응답 본문 저장
            );

    {
        // 시뮬레이션 설정: 한 번에 1000명의 사용자가 콘서트 조회 시도
        setUp(scn.injectOpen(rampUsers(3000).during(1))).protocols(httpProtocol);
    }
}
