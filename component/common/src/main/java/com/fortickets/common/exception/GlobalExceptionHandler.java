package com.fortickets.common.exception;
import feign.FeignException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortickets.common.util.CommonResponse;
import com.fortickets.common.util.CommonResponse.CommonEmptyRes;
import com.fortickets.common.util.ErrorCase;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final InvalidInputMapper mapper;
    private final HttpServletResponse response; // HttpStatus 설정을 위한 response 객체
    private final ObjectMapper objectMapper;
    /**
     * 권한 부족 (403) 발생 시 처리하는 핸들러
     */
    @ExceptionHandler(AccessDeniedException.class)
    public CommonResponse<CommonEmptyRes> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("권한 부족 예외 발생: {}", e.getMessage());
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return CommonResponse.error(ErrorCase.NOT_AUTHORIZED);
    }


    /**
     * Business 오류 발생에 대한 핸들러
     */
    @ExceptionHandler(GlobalException.class)
    public CommonResponse<CommonEmptyRes> handleGlobalException(GlobalException e) {
        response.setStatus(e.getErrorCase().getHttpStatus().value()); // HttpStatus 설정

        return CommonResponse.error(e.getErrorCase()); // 공통 응답 양식 반환
    }

    /**
     * RequestBody 입력 파라미터가 없거나 형식이 맞지 않을 때 발생하는 오류에 대한 핸들러
     */
    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class,
        HttpRequestMethodNotSupportedException.class,
        HttpMediaTypeNotAcceptableException.class,
        HttpMediaTypeNotSupportedException.class,
        MissingPathVariableException.class,
        MissingServletRequestParameterException.class
    })
    public CommonResponse<CommonEmptyRes> handleGlobalException(Exception e) {
        response.setStatus(ErrorCase.INVALID_INPUT.getHttpStatus().value()); // HttpStatus 설정

        return CommonResponse.error(ErrorCase.INVALID_INPUT); // 공통 응답 양식 반환
    }

    /**
     * Validation 라이브러리로 RequestBody 입력 파라미터 검증 오류 발생에 대한 핸들러
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResponse<List<InvalidInputRes>> handlerValidationException(MethodArgumentNotValidException e) {
        response.setStatus(ErrorCase.INVALID_INPUT.getHttpStatus().value()); // HttpStatus 설정

        // 잘못된 입력 에러들을 DTO 변환
        List<InvalidInputRes> invalidInputResList = changeFieldErrorToDto(e);

        return CommonResponse.error(ErrorCase.INVALID_INPUT, invalidInputResList); // 공통 응답 양식 반환
    }

    private List<InvalidInputRes> changeFieldErrorToDto(MethodArgumentNotValidException e) {
        return e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(mapper::toInvalidInputResponseDto) // defaultMessage 필드명을 message 변경
            .toList();
    }

    /**
     * feign client 호출 시 발생하는 오류에 대한 핸들러
     */
    @ExceptionHandler(FeignException.class)
    public CommonResponse<CommonEmptyRes> handleFeignException(FeignException e) {
        CommonResponse<CommonEmptyRes> commonResponse = null;
        log.error("Feign Client 호출 오류 발생", e);
        try {
            // FeignException에서 응답 본문을 추출하여 CommonResponse로 변환
            String responseBody = e.contentUTF8();  // 응답 바디 추출
            commonResponse = objectMapper.readValue(responseBody, CommonResponse.class);  // CommonResponse로 변환
            response.setStatus(e.status()); // HttpStatus 설정
        } catch (Exception ex) {
            // 만약 파싱 중 문제가 생기면 기본 응답 반환
            commonResponse = CommonResponse.error(ErrorCase.SYSTEM_ERROR);
        }
        return commonResponse; // 공통 응답 양식 반환
    }

    /**
     * 예상치 못한 에러 발생에 대한 핸들러
     */
    @ExceptionHandler({Exception.class, RuntimeException.class})
    public CommonResponse<CommonEmptyRes> handleException(Exception e) {
        log.error("예상치 못한 에러 발생", e);

        response.setStatus(ErrorCase.SYSTEM_ERROR.getHttpStatus().value()); // HttpStatus 설정

        return CommonResponse.error(ErrorCase.SYSTEM_ERROR); // 공통 응답 양식 반환
    }
}