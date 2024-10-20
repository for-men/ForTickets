package com.fortickets.concertservice.application.dto.response;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetConcertRes {

    private Long id;
    private String concertName;
    private String concertImage;
    private int runtime;
    private Long price;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<GetScheduleRes> schedules;
}
