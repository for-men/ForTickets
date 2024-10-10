package computerdatabase;

import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.header;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class QueueLoadTest extends Simulation {


    // HTTP 프로토콜 설정
    HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:12011") // 테스트 대상 서버 URL
        .acceptHeader("application/json")
        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjIsImVtYWlsIjoibWFuYWdlcjFAZW1haWwuY29tIiwicm9sZSI6IlJPTEVfTUFOQUdFUiIsImlzcyI6InVzZXItc2VydmljZSIsImlhdCI6MTcyODUwNjA2NCwiZXhwIjoxNzI4NTA5NjY0fQ.sPrCSGvtj5raQxr9Pu5c6iUMNjcyeqg58YJecUkYTN8");

    // 시나리오 정의
    ScenarioBuilder scn = scenario("Queue Load Test")
        // 초기 요청을 통해 대기표 발급 시도
        .exec(
            http("Initial Request without Ticket")
                .get("/order-service/bookings")
                .check(status().is(429)) // 트래픽이 많아 대기표가 발급되는지 확인
                .check(header("X-Ticket-Number").saveAs("ticketNumber")) // 대기표 ID를 저장
        )
        .pause(2) // 대기표 발급 후 2초 후 재요청
        // 발급받은 대기표를 사용하여 재요청
        .exec(
            http("Reattempt with Ticket")
                .get("/order-service/bookings")
                .header("X-Ticket-Number", "#{ticketNumber}") // 대기표 ID를 헤더에 추가
                .check(status().in(200)) // 성공적으로 처리되거나 대기열 상태 확인
        );

    {
        // 시나리오 설정: 1000명의 사용자가 30초 동안 점진적으로 부하를 줌
        setUp(
            scn.injectOpen(rampUsers(500).during(3))
        ).protocols(httpProtocol);
    }
}