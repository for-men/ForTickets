package computerdatabase;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class BookingCancelSimulation extends Simulation {

    /**
     * 예매 취소
     */
    private int scheduleId = 25008;
    // HTTP 프로토콜 설정
    HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:12011") // 기본 URL 설정
        .header("Content-Type", "application/json")
        .header("Authorization",
            "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImVtYWlsIjoidXNlcjFAZW1haWwuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlzcyI6InVzZXItc2VydmljZSIsImlhdCI6MTcyOTU3Nzg3OSwiZXhwIjoxNzI5NTgxNDc5fQ.jdHr9C04_0GExEgcPm6Bny_v9mx-uat86KjgGXxq8b4");

    // 시나리오 정의
    ScenarioBuilder scn = scenario("Booking Scenario")
        .exec(session -> session.set("scheduleId", scheduleId))
        .repeat(10).on(
            exec(http("Create Booking") // 요청 이름 설정
                .patch(session -> "/order-service/bookings/cancel/" + session.getInt("scheduleId"))
                .check(status().in(200, 400)) // 다양한 상태 코드 체크
            ).exec(session -> {
                int currentScheduleId = session.getInt("scheduleId");
                return session.set("scheduleId", currentScheduleId + 1); // 세션에 증가된 값을 다시 저장
            })
        );

    {
        // 시나리오 설정: 시나리오를 Gatling에 등록
//        setUp(scn.injectOpen(atOnceUsers(1000))).protocols(httpProtocol); // 1명의 사용자가 동시에 요청을 보냄
        // 시나리오 실행 설정
        setUp(
            scn.injectOpen(atOnceUsers(100)) // 사용자 수 설정 (예: 1명)
        ).protocols(httpProtocol);
    }
}