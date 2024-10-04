package computerdatabase;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class UserSimulation extends Simulation {

    // HTTP 프로토콜 설정
    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:12011") // user-service의 base URL
            .acceptHeader("application/json")
            .userAgentHeader("Gatling");

    // 시나리오 정의
    ScenarioBuilder scn = scenario("유저 정보 조회")
            // 로그인 요청
//            .exec(http("로그인")
//                    .post("/user-service/auth/login")
//                    .header("Content-Type", "application/json")
//                    .body(StringBody("{ \"email\": \"user1@email.com\", \"password\": \"Abcd1234!\" }")) // 적절한 사용자 정보로 변경
//                    .check(status().is(200))
//                    .check(header("Authorization").saveAs("jwtToken")) // JWT 토큰을 추출하여 "jwtToken" 변수에 저장
//            )
            // 정보 조회 요청
            .exec(http("유저 정보 조회")
                    .get("/user-service/users") // 유저 정보 조회 API 경로
//                    .header("Authorization", "#{jwtToken}") // 세션에서 jwtToken 가져오기
                    .header("Authorization", "Bearer ~") // 로그인 후 반환되는 jwt를 직접 입력
                    .check(status().is(200)) // 응답 상태 코드 체크
                    .check(bodyString().saveAs("responseBody")) // 응답 본문을 변수에 저장
            )
            // 토큰 확인
//            .exec(session -> {
//                String jwtToken = session.getString("jwtToken");
//                System.out.println("JWT Token: " + jwtToken); // JWT 토큰 출력
//                String responseBody = session.getString("responseBody");
//                System.out.println("Response Body: " + responseBody);
//                return session;
//            })
            ;
    {
        // 시뮬레이션 설정: 한 번에 10명의 사용자가 시나리오 실행
        setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
    }
}
