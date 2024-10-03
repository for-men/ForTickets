package com.fortickets.orderservice.domain.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import com.fortickets.orderservice.application.dto.request.CreatePaymentReq;
import com.fortickets.orderservice.application.dto.response.GetPaymentRes;
import com.fortickets.orderservice.domain.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = SPRING)
public interface PaymentMapper {

    Payment toEntity(CreatePaymentReq createPaymentReq);

    GetPaymentRes toGetPaymentRes(Payment payment);
}