package com.fortickets.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class GlobalUtil {

    // 좌석이 '숫자 숫자' 형식인지 확인하는 메서드
    public static boolean isValidSeatFormat(String seat) {
        // 정규 표현식: 숫자 + 공백 + 숫자
        String seatPattern = "\\d+ \\d+";
        return seat.matches(seatPattern);
    }

    // SHA-256으로 해싱
    public static String hash(String input) throws NoSuchAlgorithmException {
        // MessageDigest 객체를 SHA-256으로 초기화
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // 입력 문자열을 해싱하여 바이트 배열을 생성
        byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        // 해싱된 바이트 배열을 Base64로 인코딩하여 문자열로 변환
        return Base64.getEncoder().encodeToString(hashBytes);
    }
}