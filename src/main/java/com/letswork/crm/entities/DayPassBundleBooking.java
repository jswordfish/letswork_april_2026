package com.letswork.crm.entities;

import java.time.LocalDateTime;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.letswork.crm.enums.DayPassBundleStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("DayPassBundle")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DayPassBundleBooking extends Booking{
	
//	@ManyToOne
//	@JoinColumn(name = "day_pass_bundle_id", referencedColumnName = "id") // Add this!
//	private DayPassBundle dayPassBundle;
	
	/**
	 * Temp fix to avoid nested foreign constraint problem
	 */
	Long dayPassBundleeId;
	
	private LocalDateTime expiryDate;//this was there
	
	Integer remainingNumberOfDays;
	
	Float price;
	
	@ManyToOne
	Offers appliedOffer;
	
	Float discountedPrice;
	
	Boolean paid;
	
	 @Enumerated(EnumType.STRING)
	private DayPassBundleStatus dayPassBundleStatus;

}
