package com.letswork.crm.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.letswork.crm.enums.BookingStatus;
import com.letswork.crm.enums.BookingType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "unified_bookings")
public class UnifiedBooking {
	
	@Id
    private String id;

    private Long bookingId;

    private String companyId;
    private String email;
    private String letsWorkCentre;
    private String city;
    private String state;

    private LocalDate dateOfBooking;

    private LocalDateTime createDate;

    @Enumerated(EnumType.STRING)
    private BookingStatus currentStatus;

    @Enumerated(EnumType.STRING)
    private BookingType bookingType;

    private Integer numberOfDays;
    private String roomName;
    private Float numberOfHours;

    private String bookingCode;
    private String adminEmail;

}
