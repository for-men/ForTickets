package computerdatabase;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class BookingSimulation3 extends Simulation {

    // HTTP 프로토콜 설정
    HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:12011") // 기본 URL 설정
        .header("Content-Type", "application/json")
        .header("Authorization",
            "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImVtYWlsIjoidXNlcjEwQGVtYWlsLmNvbSIsInJvbGUiOiJST0xFX1VTRVIiLCJpc3MiOiJ1c2VyLXNlcnZpY2UiLCJpYXQiOjE3Mjk1NzUyMTMsImV4cCI6MTcyOTU3ODgxM30.MFqF5WjTFFK88bSax-K9tfukF84W8c1Rd5x40wwfdIM");

    // 고정된 값 설정
    private final int concertId = 1; // 고정된 concertId
    private final int scheduleId = 1; // 고정된 scheduleId
    private final int userId = 1; // 고정된 userId
    private final int price = 90000; // 고정된 가격
    private final String seat = "1 1"; // 고정된 좌석

    // 시나리오 정의
    ScenarioBuilder scn = scenario("Booking Scenario")
        .repeat(1).on( // 10번 반복
            exec(http("Create Booking") // 요청 이름 설정
                .post("/order-service/bookings") // 요청 경로
                .body(StringBody("{ \"scheduleId\": " + scheduleId +
                    ", \"concertId\": " + concertId +
                    ", \"userId\": " + userId +
                    ", \"price\": " + price +
                    ", \"seat\": [ \"" + seat + "\" ] }"))
                .check(status().in(200, 202, 400)) // 다양한 상태 코드 체크
            ));

    {
        // 시나리오 설정: 시나리오를 Gatling에 등록
        setUp(
            scn.injectOpen(rampUsers(3000).during(1))
        ).protocols(httpProtocol);
    }
}
