package com.letswork.crm.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("ConferenceBundleBooking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ConferenceBundleBooking extends Booking{
	
	@ManyToOne
	private ConferenceBundle conferenceBundle;

    private Float remainingHours;

    

    /**
     * Price at the time of booking.
     */
    private BigDecimal price;

    /**
     * Price at the time of booking.
     */
    private BigDecimal discountedPrice;

}
