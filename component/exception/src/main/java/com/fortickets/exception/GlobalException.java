package com.fortickets.exception;

import com.fortickets.common.ErrorCase;
import lombok.Getter;

/**
 * 전역 예외 처리를 위한 커스텀 예외 클래스 - ResultCase 받아서 처리
 */
@Getter
public class GlobalException extends RuntimeException {

    private final ErrorCase errorCase;

    public GlobalException(ErrorCase errorCase) {
        super(errorCase.getMessage());
        this.errorCase = errorCase;
    }
}