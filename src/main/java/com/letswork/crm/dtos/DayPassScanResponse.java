package com.letswork.crm.dtos;

import java.time.LocalDateTime;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DayPassScanResponse {

    private String bookingCode;
    private String email;
    private Integer numberOfDays;
    private String centre;
    private Integer used;

//    public static DayPassScanResponse from(BookDayPass b) {
//        return DayPassScanResponse.builder()
//                .bookingCode(b.getBookingCode())
//                .email(b.getEmail())
//                .numberOfDays(b.getNumberOfDays())
//                .centre(b.getLetsWorkCentre())
//                .used(b.getUsed())
//                .build();
//    }
}
