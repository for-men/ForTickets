package computerdatabase;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class BookingSimulation2 extends Simulation {

    // HTTP 프로토콜 설정
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:12011") // 기본 URL 설정
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjIsImVtYWlsIjoidXNlcjFAZW1haWwuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlzcyI6InVzZXItc2VydmljZSIsImlhdCI6MTcyODI3MTk3NCwiZXhwIjoxNzI4Mjc1NTc0fQ.l50RwXym19FeCUzWn6Jx2UGw267H1YfkAs2WqrHsNvY");

    // 시나리오 정의
    ScenarioBuilder scn = scenario("Booking Scenario")
            .exec(http("Create Booking") // 요청 이름 설정
                    .post("/order-service/bookings") // 요청 경로
                    .body(StringBody("{ \"scheduleId\": 1, \"concertId\": 1, \"userId\": 2, \"price\": 90000, \"seat\": [ \"3 3\" ] }"))
                    .check(status().in(200, 400)) // 200, 400일 때 체크
            );

    {
        // 시나리오 설정: 시나리오를 Gatling에 등록
        setUp(scn.injectOpen(atOnceUsers(1000))).protocols(httpProtocol); // 1명의 사용자가 동시에 요청을 보냄
//        setUp(scn.injectOpen(rampUsers(1000).during(10))).protocols(httpProtocol); // 10초동안 1000건의 요청

    }
}
