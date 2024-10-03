package com.fortickets.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookingStatus {
    PENDING("예매 대기"),
    CONFIRMED("예매 확정"),
    CANCELED("예매 취소");

    private final String status;
}
