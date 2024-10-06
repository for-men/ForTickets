package computerdatabase;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.bodyString;
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

    // 시나리오 정의
    ScenarioBuilder scn = scenario("스케줄 조회 시나리오")
            // 1. 데이터 생성
            .exec(session -> {
                // JWT 토큰을 포스트맨에서 복사하여 입력
                String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjIsImVtYWlsIjoidXNlcjFAZW1haWwuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlzcyI6InVzZXItc2VydmljZSIsImlhdCI6MTcyODIxMjMzNSwiZXhwIjoxNzI4MjE1OTM1fQ.Je_sMmxnCxYRrR6O112nOD8Euk1T9dBjOL-9PoZvCw0"; // 포스트맨에서 받은 JWT 토큰
                Long scheduleId = 1L; // 고정된 스케줄 ID
                System.out.println("Using fixed JWT Token: " + jwtToken); // 추가된 로그
                System.out.println("Using fixed scheduleId: " + scheduleId); // 추가된 로그
                return session.set("jwtToken", jwtToken).set("scheduleId", scheduleId); // 세션에 토큰과 스케줄 ID 저장
            })

            // 2. 스케줄 조회 요청
            .exec(http("스케줄 조회 요청")
                    .get("/concert-service/schedules/#{scheduleId}") // 스케줄 조회 API
                    .header("Authorization", "Bearer #{jwtToken}") // 동적으로 생성된 JWT 토큰 사용
                    .check(status().is(200)) // 응답 상태 코드 체크
                    .check(bodyString().saveAs("responseBody")) // 응답 본문 저장
            )

            // 3. 응답 내용 출력
            .exec(session -> {
                String jwtToken = session.getString("jwtToken");
                Long scheduleId = session.getLong("scheduleId");
                System.out.println("Requesting with JWT Token: " + jwtToken); // 요청할 JWT 토큰 로그
                System.out.println("Requesting scheduleId: " + scheduleId); // 요청할 scheduleId 로그
                return session;
            })
            .exec(session -> {
                String responseBody = session.getString("responseBody");
                System.out.println("Response Body: " + responseBody); // 응답 본문 출력
                return session;
            });

    {
        // 시뮬레이션 설정: 한 번에 n명의 사용자가 스케줄 조회 시도
        setUp(scn.injectOpen(atOnceUsers(10))).protocols(httpProtocol);
    }
}
