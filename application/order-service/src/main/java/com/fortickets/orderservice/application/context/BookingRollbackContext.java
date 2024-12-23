package com.fortickets.orderservice.application.context;

import com.fortickets.orderservice.application.dto.request.DecrementSeatsRollbackReq;
import com.fortickets.orderservice.application.dto.request.DeleteBookingsRollbackReq;
import java.util.List;
import lombok.Getter;

@Getter
public class BookingRollbackContext {

    // 시트 롤백
    private DecrementSeatsRollbackReq decrementSeatsRollbackReq;
    // 예매 롤백
    private DeleteBookingsRollbackReq deleteBookingsRollbackReq;

    public void addDecrementSchedule(Integer quantity, Long scheduleId) {
        this.decrementSeatsRollbackReq = new DecrementSeatsRollbackReq(quantity, scheduleId);
    }

    public void addCreateBooking(List<Long> ids) {
        this.deleteBookingsRollbackReq = new DeleteBookingsRollbackReq(ids);
    }
}
