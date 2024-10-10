package computerdatabase;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.util.concurrent.ThreadLocalRandom;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class BookingSimulation2 extends Simulation {

    // HTTP 프로토콜 설정
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:12011") // 기본 URL 설정
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImVtYWlsIjoidXNlcjFAZW1haWwuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlzcyI6InVzZXItc2VydmljZSIsImlhdCI6MTcyODQ4Mzg4NiwiZXhwIjoxNzI4NDg3NDg2fQ.ARzk6IHTb-mySGqP7lOrOAHwISd1EfuPtCoG4TyzYeo");

    private final int[] concertIds = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private final int[][] scheduleIds = {
        {1, 2, 3, 4, 5},   // concert_id 1
        {6, 7, 8, 9, 10},  // concert_id 2
        {11, 12, 13, 14, 15}, // concert_id 3
        {16, 17, 18, 19, 20}, // concert_id 4
        {21, 22, 23, 24, 25}, // concert_id 5
        {26, 27, 28, 29, 30}, // concert_id 6
        {31, 32, 33, 34, 35}, // concert_id 7
        {36, 37, 38, 39, 40}, // concert_id 8
        {41, 42, 43, 44, 45}, // concert_id 9
        {46, 47, 48, 49, 50}  // concert_id 10
    };

    // 랜덤하게 concert_id 선택
    private int getRandomConcertId() {
        return concertIds[ThreadLocalRandom.current().nextInt(concertIds.length)];
    }

    // concert_id에 따라 schedule_id 선택
    private int getRandomScheduleId(int concertId) {
        return scheduleIds[concertId - 1][ThreadLocalRandom.current().nextInt(scheduleIds[concertId - 1].length)];
    }

    // 고정된 userId
    private int getUserId() {
        return 1; // userId를 1로 고정
    }

    // 랜덤하게 userId 선택 (1~2)
//    private int getRandomUserId() {
//        return ThreadLocalRandom.current().nextInt(1, 3);
//    }

    // 고정 가격 설정
    private int getRandomPrice() {
        return 90000; // 가격은 고정
    }

    // 랜덤 좌석 설정 (1~10)
    private String getRandomSeat() {
        int row = ThreadLocalRandom.current().nextInt(1, 11); // 1~10행
        int seat = ThreadLocalRandom.current().nextInt(1, 11); // 1~10열
        return row + " " + seat; // "행 열" 형태로 반환
    }

    // 시나리오 정의
    ScenarioBuilder scn = scenario("Booking Scenario")
        .repeat(1000)
        .on(
            exec(http("Create Booking") // 요청 이름 설정
                .post("/order-service/bookings") // 요청 경로
                .body(StringBody("{ \"scheduleId\": " + getRandomScheduleId(getRandomConcertId()) +
                    ", \"concertId\": " + getRandomConcertId() +
                    ", \"userId\": " + getUserId() +
                    ", \"price\": " + getRandomPrice() +
                    ", \"seat\": [ \"" + getRandomSeat() + "\" ] }"))
                .check(status().in(200, 400)) // 200, 400일 때 체크
            ));

    {
        // 시나리오 설정: 시나리오를 Gatling에 등록
//        setUp(scn.injectOpen(atOnceUsers(1000))).protocols(httpProtocol); // 1명의 사용자가 동시에 요청을 보냄
        setUp(scn.injectOpen(rampUsers(5).during(1))).protocols(httpProtocol); // 10초동안 1000건의 요청

    }
}