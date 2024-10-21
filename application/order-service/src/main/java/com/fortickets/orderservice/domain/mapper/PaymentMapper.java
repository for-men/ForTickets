package com.fortickets.orderservice.domain.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import com.fortickets.orderservice.application.dto.CreatePaymentRes;
import com.fortickets.orderservice.application.dto.request.CreatePaymentReq;
import com.fortickets.orderservice.application.dto.response.GetConcertRes;
import com.fortickets.orderservice.application.dto.response.GetPaymentDetailRes;
import com.fortickets.orderservice.application.dto.response.GetPaymentRes;
import com.fortickets.orderservice.application.dto.response.GetScheduleDetailRes;
import com.fortickets.orderservice.application.dto.response.GetUserRes;
import com.fortickets.orderservice.domain.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = SPRING)
public interface PaymentMapper {

    Payment toEntity(CreatePaymentReq createPaymentReq);

    GetPaymentRes toGetPaymentRes(Payment payment);

    @Mapping(source = "payment.concertId", target = "concertId")
    GetPaymentRes toGetPaymentUser(Payment payment, GetConcertRes concertRes);

    @Mapping(source = "payment.id", target = "id")
    @Mapping(source = "payment.concertId", target = "concertId")
    @Mapping(source = "payment.userId", target = "userId")
    GetPaymentDetailRes toGetPaymentDetailRes(Payment payment, GetScheduleDetailRes getScheduleDetailRes, GetUserRes getUserRes);

    @Mapping(source = "payment.id", target = "paymentId")
    CreatePaymentRes toCreatePaymentRes(Payment payment);
}