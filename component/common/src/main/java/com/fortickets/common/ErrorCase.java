package com.fortickets.common;

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
  //중복된 email 400
  DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, 2002, "중복된 Email 입니다."),

  /* Concert 3000번대 */
  NOT_EXIST_SCHEDULE(HttpStatus.NOT_FOUND, 3000, "존재하지 않는 스케줄입니다."),

  /* Order 4000번대 */
  ALREADY_BOOKED_SEAT(HttpStatus.BAD_REQUEST, 4000, "이미 예약된 좌석입니다."),
  BOOKING_NOT_FOUND(HttpStatus.NOT_FOUND, 4001, "예약을 찾을 수 없습니다."),

  ;






  private final HttpStatus httpStatus; // 응답 상태 코드
  private final Integer code; // 응답 코드. 도메인에 따라 1000번대로 나뉨
  private final String message; // 응답에 대한 설명
}
