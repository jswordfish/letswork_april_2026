package com.letswork.crm.dtos;

import java.time.LocalDate;

import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.BookingType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnifiedBookingDto {

    private Long bookingId;
    private BookingType bookingType;

    private String email;
    private String letsWorkCentre;
    private String city;
    private String state;

    private LocalDate dateOfBooking;

    private BookingStatus currentStatus;

    // Day pass specific
    private Integer numberOfDays;

    // Conference specific
    private String roomName;
    private Float numberOfHours;
}
