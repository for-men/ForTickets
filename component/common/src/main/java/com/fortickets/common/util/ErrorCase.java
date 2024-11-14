package com.fortickets.common.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCase {

    /* 글로벌 1000번대 */

    // 권한 없음 403
    NOT_AUTHORIZED(HttpStatus.FORBIDDEN, 1000, "해당 요청에 대한 권한이 없습니다."),
    // 잘못된 형식의 입력 400
    INVALID_INPUT(HttpStatus.BAD_REQUEST, 1001, "유효하지 않은 입력값입니다."),
    // 존재하지 않는 값 404
    NOT_FOUND(HttpStatus.NOT_FOUND, 1002, "존재하지 않는 입력값입니다."),
    // 시스템 에러 500
    SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1003, "알 수 없는 에러가 발생했습니다."),

    /* User 2000번대 */

    // 존재하지 않는 사용자 404,
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 2000, "유저를 찾을 수 없습니다."),
    // 로그인 필요 401
    LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, 2001, "로그인이 필요합니다."),
    //중복된 email 409 -> 기존 400에서 409로 수정했습니다.
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, 2002, "중복된 Email 입니다."),
    // null값 400
    EMPTY_EMAIL_OR_PASSWORD(HttpStatus.BAD_REQUEST, 2003, "이메일 또는 비밀번호가 비어있습니다."),
    // 401
    INVALID_EMAIL_OR_PASSWORD(HttpStatus.UNAUTHORIZED, 2004, "잘못된 이메일 혹은 비밀번호를 입력했습니다."),
    // 409
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, 2005, "중복된 닉네임이 존재합니다."),
    // 409
    DUPLICATE_PHONE(HttpStatus.CONFLICT, 2005, "중복된 번호가 존재합니다."),
    // 400
    INVALID_SELLER_CODE(HttpStatus.BAD_REQUEST, 2006, "잘못된 판매자 코드입니다."),
    // 400
    INVALID_MANAGER_CODE(HttpStatus.BAD_REQUEST, 2007, "잘못된 관리자 코드입니다."),

    /* Concert 3000번대 */

    NOT_EXIST_SCHEDULE(HttpStatus.NOT_FOUND, 3000, "존재하지 않는 스케줄입니다."),
    NOT_EXIST_STAGE(HttpStatus.NOT_FOUND, 3001, "존재하지 않는 공연장입니다."),
    NOT_EXIST_CONCERT(HttpStatus.NOT_FOUND, 3002, "존재하지 않는 공연입니다."),
    NOT_PERMITTED_TO_ADD_SCHEDULE(HttpStatus.FORBIDDEN, 3003, "해당 공연에 대한 스케줄을 추가할 권한이 없습니다."),
    // 400
    SCHEDULE_START_DATE_TOO_EARLY(HttpStatus.BAD_REQUEST, 3004, "스케줄은 공연 시작 날짜보다 빠를 수 없습니다."),
    // 400
    SCHEDULE_START_DATE_TOO_LATE(HttpStatus.BAD_REQUEST, 3005, "스케줄은 공연 종료 날짜보다 늦을 수 없습니다."),

    /* Order 4000번대 */

    ALREADY_BOOKED_SEAT(HttpStatus.BAD_REQUEST, 4000, "이미 예매가 완료된 좌석입니다."),
    BOOKING_NOT_FOUND(HttpStatus.NOT_FOUND, 4001, "예매를 찾을 수 없습니다."),
    CANNOT_CANCEL_BOOKING(HttpStatus.BAD_REQUEST, 4002, "취소할 수 없는 예매입니다."),
    NOT_FOUND_PAYMENT(HttpStatus.NOT_FOUND, 4003, "결제 내역을 찾을 수 없습니다."),
    DUPLICATE_SEAT(HttpStatus.BAD_REQUEST, 4004, "중복된 좌석이 존재합니다."),
    INVALID_SEAT_FORMAT(HttpStatus.BAD_REQUEST, 4005, "좌석 형식이 잘못되었습니다."),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, 4006, "결제에 실패했습니다.")

    ;

    private final HttpStatus httpStatus; // 응답 상태 코드
    private final Integer code; // 응답 코드. 도메인에 따라 1000번대로 나뉨
    private final String message; // 응답에 대한 설명
}
