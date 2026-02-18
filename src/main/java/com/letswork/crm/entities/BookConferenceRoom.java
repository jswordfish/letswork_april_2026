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

import com.letswork.crm.enums.BookingStatus;

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
public class BookConferenceRoom extends Base{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private LocalDateTime dateOfPurchase;
	
	private LocalDate dateOfBooking;
	
	private Integer numberOfGuests;
		
	private Float numberOfHours;
	
	private String email;
	
	private Boolean bundleUsed;
	
	private String letsWorkCentre;
	
	private String city;
	
	private String state;
	
	private String roomName;
	
	private String bookingCode;   
	
	private String qrS3Path;    
	
	private Boolean used;
	
	@Enumerated(EnumType.STRING)  
    private BookingStatus currentStatus;
	
	@OneToMany(
	        mappedBy = "booking",
	        cascade = CascadeType.ALL,
	        orphanRemoval = true
	    )
	    private List<ConferenceRoomTimeSlot> slots = new ArrayList<>();

}
