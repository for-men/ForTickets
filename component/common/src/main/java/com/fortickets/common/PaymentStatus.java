package com.fortickets.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    WAITING ("결제 대기"),
    COMPLETED ("결제 완료"),
    FAILED ("결제 실패"),
    CANCELED ("결제 취소"),
    PART_CANCELED ("부분 취소");

    private final String status;
}
