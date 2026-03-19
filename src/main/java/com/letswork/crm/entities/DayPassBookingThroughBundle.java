package com.letswork.crm.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

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
	
	private Integer numberOfDays;
	
	private LocalDate dateOfUse;
	
	@ManyToOne
	DayPassBundleBooking dayPassBundle;

}
