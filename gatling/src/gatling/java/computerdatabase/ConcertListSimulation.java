package computerdatabase;

import static io.gatling.javaapi.core.CoreDsl.bodyString;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class ConcertListSimulation extends Simulation {

    /**
     * 공연 리스트 조회
     */
    // HTTP 프로토콜 설정
    HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:12011") // API의 기본 URL, 환경에 맞게 설정
        .acceptHeader("application/json")
        .contentTypeHeader("application/json")
        .userAgentHeader("Gatling");

    // 시나리오 정의
    ScenarioBuilder scn = scenario("Concert 리스트 조회 시나리오")
        .repeat(100).on(
            // 1. 데이터 생성
            exec(session -> {
                // JWT 토큰을 포스트맨에서 복사하여 입력
                String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImVtYWlsIjoidXNlcjFAZW1haWwuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlzcyI6InVzZXItc2VydmljZSIsImlhdCI6MTcyOTU2OTA3MywiZXhwIjoxNzI5NTcyNjczfQ.IgyEmZHtWe2QdkYPXVtqgA_Qo5ASAg8iIcbuWN8r70E";
                return session.set("jwtToken", jwtToken); // 세션에 토큰과 콘서트 ID 저장
            })
                // 2. Concert 조회 요청
                .exec(http("Concert 목록 조회 요청")
                    .get("/concert-service/concerts") // 콘서트 조회 API
                    .header("Authorization", "Bearer #{jwtToken}") // 동적으로 생성된 JWT 토큰 사용
                    .check(status().is(200)) // 응답 상태 코드 체크
                    .check(bodyString().saveAs("responseBody")) // 응답 본문 저장
                ));

    {
        // 시뮬레이션 설정: 한 번에 1000명의 사용자가 콘서트 조회 시도
//        setUp(scn.injectOpen(rampUsers(10).during(1))).protocols(httpProtocol);
        setUp(
            scn.injectOpen(OpenInjectionStep.atOnceUsers(100)) // 사용자 수 설정 (예: 1명)
        ).protocols(httpProtocol);
    }

}
