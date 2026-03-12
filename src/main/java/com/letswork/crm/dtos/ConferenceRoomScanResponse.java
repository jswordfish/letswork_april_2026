package com.letswork.crm.dtos;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConferenceRoomScanResponse {

    private String bookingCode;
    private String email;
    private String roomName;
    private String letsWorkCentre;
    private String city;
    private String state;
    private Float numberOfHours;
    private Boolean used;
    private LocalDateTime dateOfPurchase;

//    public static ConferenceRoomScanResponse from(
//            BookConferenceRoom booking
//    ) {
//        return new ConferenceRoomScanResponse(
//                booking.getBookingCode(),
//                booking.getEmail(),
//                booking.getRoomName(),
//                booking.getLetsWorkCentre(),
//                booking.getCity(),
//                booking.getState(),
//                booking.getNumberOfHours(),
//                booking.getUsed(),
//                booking.getDateOfPurchase()
//        );
//    }
}
