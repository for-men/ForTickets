package computerdatabase;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;


import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class UserSignUpSimulation2 extends Simulation {

    // 기본 HTTP 설정
    HttpProtocolBuilder httpProtocol =
        http.baseUrl("http://localhost:12011")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");


    // 임의의 사용자 데이터를 생성하는 함수
    ScenarioBuilder signUpScenario = scenario("회원가입")
        .exec(http("User Registration")
            .post("/user-service/auth/sign-up")
            .body(StringBody("{\"nickname\": \"#{nickname}\", \"email\": \"#{email}\", \"password\": \"#{password}\", \"phone\": \"#{phone}\", \"isSeller\": true, \"sellerToken\": \"AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC\"}"))
            .asJson() // JSON 형태로 요청
        );

    {
        setUp(signUpScenario.injectOpen(atOnceUsers(1)).protocols(httpProtocol));
    }
}

