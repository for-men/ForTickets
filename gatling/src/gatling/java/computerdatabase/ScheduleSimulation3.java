package computerdatabase;

import io.gatling.javaapi.core.OpenInjectionStep;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.util.concurrent.ThreadLocalRandom;
import static io.gatling.javaapi.core.CoreDsl.bodyString;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class ScheduleSimulation3 extends Simulation {

    // HTTP 프로토콜 설정
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:12011") // API의 기본 URL
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .userAgentHeader("Gatling");

    // 랜덤 스케줄 아이디 생성
    private Long getRandomScheduleId() {
        return (long) ThreadLocalRandom.current().nextInt(1, 501); // 1부터 50까지의 랜덤 Long 값 생성
    }

    // 시나리오 정의
    ScenarioBuilder scn = scenario("스케줄 조회 시나리오")
        .repeat(10).on(
            // 1. 데이터 생성
            exec(session -> {
                // JWT 토큰을 포스트맨에서 복사하여 입력
                String jwtToken = " eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImVtYWlsIjoidXNlcjFAZW1haWwuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlzcyI6InVzZXItc2VydmljZSIsImlhdCI6MTcyOTQwMjYzNywiZXhwIjoxNzI5NDA2MjM3fQ.9yhDzok69QIbqtQiotIkE8MGxBWyVR5kHk2RCh6ETuE";
                Long scheduleId = getRandomScheduleId(); // 랜덤한 스케줄 ID 생성
                return session.set("jwtToken", jwtToken).set("scheduleId", scheduleId); // 세션에 토큰과 스케줄 ID 저장
            })

                // 2. 스케줄 조회 요청
                .exec(http("스케줄 조회 요청")
                    .get("/concert-service/schedules/#{scheduleId}") // 스케줄 조회 API
                    .header("Authorization", "Bearer #{jwtToken}") // 동적으로 생성된 JWT 토큰 사용
                    .check(status().is(200)) // 응답 상태 코드 체크
                    .check(bodyString().saveAs("responseBody")) // 응답 본문 저장
                ));

    {
        // 시뮬레이션 설정: 한 번에 n명의 사용자가 스케줄 조회 시도
        setUp(
            scn.injectOpen(OpenInjectionStep.atOnceUsers(1000)) // 사용자 수 설정 (예: 1명)
        ).protocols(httpProtocol);
    }
}

