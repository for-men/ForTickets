//package computerdatabase;
//
//import io.gatling.javaapi.core.Simulation;
//import io.gatling.javaapi.core.ScenarioBuilder;
//import io.gatling.javaapi.http.HttpProtocolBuilder;
//
//import static io.gatling.javaapi.core.CoreDsl.scenario;
//import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
//import static io.gatling.javaapi.http.HttpDsl.http;
//import static io.gatling.javaapi.http.HttpDsl.status;
//
//public class BookingSimulation1 extends Simulation {
//
//    // HTTP 프로토콜 설정
//    HttpProtocolBuilder httpProtocol = http
//            .baseUrl("http://localhost:12011") // 기본 URL 설정
//            .header("Content-Type", "application/json")
//            .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjIsImVtYWlsIjoidXNlcjFAZW1haWwuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlzcyI6InVzZXItc2VydmljZSIsImlhdCI6MTcyODE5OTMyMCwiZXhwIjoxNzI4MjAyOTIwfQ.NsFMYT6V8U7xgDeAqbMRAaDXDcOpYaVd90fRRLdudxg");
//
//    // 시나리오 정의
//    ScenarioBuilder scn = scenario("Booking Scenario")
//            .exec(http("Create Booking") // 요청 이름 설정
//                    .post("/order-service/bookings") // 요청 경로
//                    .bodyString("{ \"scheduleId\": 1, \"concertId\": 1, \"userId\": 2, \"price\": 90000, \"seat\": [ \"3 3\" ] }") // 요청 본문
//                    .check(status().is(200))); // 응답 코드 체크
//
//    {
//        // 시나리오 설정: 시나리오를 Gatling에 등록
//        setUp(scn.injectOpen(atOnceUsers(1))) // 1명의 사용자가 동시에 요청을 보냄
//                .protocols(httpProtocol); // HTTP 프로토콜 설정 추가
//    }
//}
