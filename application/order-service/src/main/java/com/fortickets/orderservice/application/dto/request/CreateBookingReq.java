package com.fortickets.orderservice.application.dto.request;


import com.fortickets.orderservice.domain.entity.Booking;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateBookingReq(
    @NotNull Long scheduleId,
    @NotNull Long userId,
    @NotNull Long price,
    @NotNull List<String> seat
) {

    public Booking toEntity(String seat) {
        return new Booking(scheduleId(), userId(), price(), seat);
    }
}
