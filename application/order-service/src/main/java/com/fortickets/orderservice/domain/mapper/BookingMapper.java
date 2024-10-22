package com.fortickets.orderservice.domain.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import com.fortickets.orderservice.application.dto.response.CreateBookingRes;
import com.fortickets.orderservice.application.dto.response.GetBookingRes;
import com.fortickets.orderservice.application.dto.response.GetConcertDetailRes;
import com.fortickets.orderservice.application.dto.response.GetConcertRes;
import com.fortickets.orderservice.application.dto.response.GetScheduleDetailRes;
import com.fortickets.orderservice.domain.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = SPRING)
public interface BookingMapper {

    CreateBookingRes toCreateBookingRes(Booking booking);

    @Mapping(source = "booking.concertId", target = "concertId")
    @Mapping(source = "booking.id", target = "id")
    @Mapping(source = "booking.price", target = "price")
    @Mapping(source = "booking.payment.id", target = "paymentId")
    GetBookingRes toGetBookingRes(Booking booking, GetConcertRes concertRes);

    @Mapping(source = "booking.concertId", target = "concertId")
    @Mapping(source = "booking.scheduleId", target = "scheduleId")
    @Mapping(source = "booking.payment.id", target = "paymentId")
    GetConcertDetailRes toGetConcertDetailRes(Booking booking, GetScheduleDetailRes getScheduleDetailRes);
}