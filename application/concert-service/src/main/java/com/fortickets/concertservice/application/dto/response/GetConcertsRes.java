package com.fortickets.concertservice.application.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetConcertsRes {
    private Long id;
    private Long userId;
    private String concertName;
    private String concertImage;
    private int runtime;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long price;

}
