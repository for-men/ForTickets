package com.fortickets.common;

public class GlobalUtil {

    // 좌석이 '숫자 숫자' 형식인지 확인하는 메서드
    public static boolean isValidSeatFormat(String seat) {
        // 정규 표현식: 숫자 + 공백 + 숫자
        String seatPattern = "\\d+ \\d+";
        return seat.matches(seatPattern);
    }

}
