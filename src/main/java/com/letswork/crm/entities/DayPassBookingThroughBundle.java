package com.letswork.crm.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import com.letswork.crm.enums.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("DayPassBookingThroughBundle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DayPassBookingThroughBundle extends Booking{
	
	private Integer numberOfPasses;
	
	
//	@ManyToOne
//	DayPassBundleBooking dayPassBundle;
	/**
	 * Temp fix...since there's a problem with nested booking id in hibernate
	 */
	Long dayPassBundleBookingId;
	
	Long previousBookingId;
	
	@ManyToOne
	LetsWorkCentre letsWorkCentre;
	
	String qrS3Path;

//	@Enumerated(EnumType.STRING)
//	private BookingStatus bookingStatus;
}
