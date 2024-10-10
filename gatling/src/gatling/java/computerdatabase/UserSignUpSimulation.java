package computerdatabase;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.core.body.StringBody;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.util.ArrayList;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class UserSignUpSimulation extends Simulation {

    // 기본 HTTP 설정
    HttpProtocolBuilder httpProtocol =
        http.baseUrl("http://localhost:12011")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // Feeder로 사용할 랜덤 데이터 생성 메서드
    private Iterator<Map<String, Object>> createFeeder() {
        List<Map<String, Object>> feederData = new ArrayList<>();
        for (int i = 0; i < 100; i++) {  // 100명의 데이터를 생성
            Map<String, Object> data = new HashMap<>();
            data.put("email", getRandomEmail());
            data.put("nickname", getRandomNickname());
            data.put("phone", getRandomPhone());  // 랜덤 전화번호 추가
            data.put("password", "Abcd1234!");  // 고정된 비밀번호 추가
            feederData.add(data);
        }
        return feederData.iterator();  // Feeder는 Iterator로 반환해야 함
    }

    // 랜덤 이메일 생성
    private String getRandomEmail() {
        int randomNum = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "user" + randomNum + "@example.com";
    }

    // 랜덤 사용자 이름 생성
    private String getRandomNickname() {
        int randomNum = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "user" + randomNum;  // 예: user6f8f7b8d
    }

    // 11자리 전화번호 생성
    private String getRandomPhone() {
        StringBuilder phoneNumber = new StringBuilder();
        // 1~9 사이의 숫자로 시작
        phoneNumber.append(ThreadLocalRandom.current().nextInt(1, 10));
        for (int i = 1; i < 11; i++) { // 두 번째 자리부터 총 10자리 추가
            phoneNumber.append(ThreadLocalRandom.current().nextInt(0, 10));  // 0부터 9까지의 랜덤 숫자 추가
        }
        return phoneNumber.toString();
    }


    // 임의의 사용자 데이터를 생성하는 함수
    ScenarioBuilder signUpScenario = scenario("회원가입")
        .feed(createFeeder())  // Feeder를 사용하여 랜덤 데이터를 공급
        .exec(http("User Registration")
            .post("/user-service/auth/sign-up")
            .body(StringBody("{\"nickname\": \"#{nickname}\", \"email\": \"#{email}\", \"password\": \"Abcd1234!\", \"phone\": \"#{phone}\"}"))
            .asJson()  // JSON 형태로 요청
        );

    {
        setUp(signUpScenario.injectOpen(atOnceUsers(100)).protocols(httpProtocol));
    }
}

