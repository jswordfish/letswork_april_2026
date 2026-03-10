package com.letswork.crm.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.letswork.crm.enums.BookedFrom;
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
public class AllBookings extends Base{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String bookingCode;

    private String email;

    private String letsWorkCentre;

    private String city;

    private String state;

    private String qrS3Path;

    private Integer amount;

    private Long previousBookingId;

    private String adminEmail;

    private LocalDateTime dateOfPurchase;

    private LocalDate dateOfBooking;
    
    private String referenceId;

    @Enumerated(EnumType.STRING)
    private BookingType bookingType;

    @Enumerated(EnumType.STRING)
    private BookingStatus currentStatus;

    @Enumerated(EnumType.STRING)
    private BookedFrom bookedFrom;

    /*
     DAY PASS FIELDS
     */

    private Integer numberOfDays;

    private Integer used;

    /*
     CONFERENCE ROOM FIELDS
     */

    private String roomName;

    private Integer numberOfGuests;

    private Float numberOfHours;
    
    private Boolean bundleUsed;

    /*
     SLOT MAPPING
     */

    @OneToMany(mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ConferenceRoomTimeSlot> slots = new ArrayList<>();


}
