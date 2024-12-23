package com.fortickets.orderservice.application.dto.request;

import java.util.List;

public record DeleteBookingsRollbackReq(
    List<Long> ids
) {

}
