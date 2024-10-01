package com.fortickets.orderservice.domain.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import com.fortickets.orderservice.application.dto.request.CreateBookingReq;
import com.fortickets.orderservice.application.dto.response.CreateBookingRes;
import com.fortickets.orderservice.domain.entity.Booking;
import org.mapstruct.Mapper;

@Mapper(componentModel = SPRING)
public interface BookingMapper {

    Booking toBooking(CreateBookingReq createBookingReq);

    CreateBookingRes toCreateBookingRes(Booking booking);
}