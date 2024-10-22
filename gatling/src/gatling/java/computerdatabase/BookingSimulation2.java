package computerdatabase;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class BookingSimulation2 extends Simulation {

    // HTTP 프로토콜 설정
    HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:12011") // 기본 URL 설정
        .header("Content-Type", "application/json")
        .header("Authorization",
            "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImVtYWlsIjoidXNlcjFAZW1haWwuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlzcyI6InVzZXItc2VydmljZSIsImlhdCI6MTcyOTUyMDc4NSwiZXhwIjoxNzI5NTI0Mzg1fQ.AryY2lcQJfIjeE0TB7fPK_t72gef3GJ-lsEeicF-LEA");

    // concertId와 scheduleId를 동적으로 매칭하는 방식
    private Map<Integer, int[]> concertScheduleMap = new HashMap<>();

    public BookingSimulation2() {
        // 콘서트 ID와 스케줄 ID 매핑
        for (int i = 1; i <= 20; i++) {
            int[] scheduleIds = new int[25];
            for (int j = 0; j < 25; j++) {
                scheduleIds[j] = (i - 1) * 25 + j + 1; // 각 콘서트마다 스케줄 ID 할당
            }
            concertScheduleMap.put(i, scheduleIds);
        }
    }

    // 랜덤하게 concertId 선택
    private int getRandomConcertId() {
        return ThreadLocalRandom.current().nextInt(1, concertScheduleMap.size() + 1);
    }

    // 선택된 concertId에 맞는 scheduleId 반환
    private int getRandomScheduleId(int concertId) {
        int[] schedules = concertScheduleMap.get(concertId);
        return schedules[ThreadLocalRandom.current().nextInt(schedules.length)];

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
        int seat = ThreadLocalRandom.current().nextInt(1, 101); // 1~10열

        return row + " " + seat; // "행 열" 형태로 반환
    }

    // 시나리오 정의
    ScenarioBuilder scn = scenario("Booking Scenario")
        .repeat(100).on( // 10번 반복
            exec(session -> {
                // concertId와 scheduleId를 한 번만 호출하고 저장
                int concertId = getRandomConcertId();
                int scheduleId = getRandomScheduleId(concertId);

                return session.set("concertId", concertId)
                    .set("scheduleId", scheduleId);
            })
                .exec(http("Create Booking") // 요청 이름 설정
                    .post("/order-service/bookings") // 요청 경로
                    .body(StringBody(session -> "{ \"scheduleId\": " + session.getInt("scheduleId") +
                        ", \"concertId\": " + session.getInt("concertId") +
                        ", \"userId\": " + getUserId() +
                        ", \"price\": " + getRandomPrice() +
                        ", \"seat\": [ \"" + getRandomSeat() + "\" ] }"))
                    .check(status().in(200, 400)) // 다양한 상태 코드 체크
                ));

    {
        // 시나리오 설정: 시나리오를 Gatling에 등록
//        setUp(scn.injectOpen(atOnceUsers(1000))).protocols(httpProtocol); // 1명의 사용자가 동시에 요청을 보냄
        // 시나리오 실행 설정
        setUp(
            scn.injectOpen(atOnceUsers(100)) // 사용자 수 설정 (예: 1명)
        ).protocols(httpProtocol);
    }
}