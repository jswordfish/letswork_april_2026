package com.letswork.crm.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("RecurringMonthlyBooking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecurringMonthlyBooking extends Booking{
	
	@OneToMany(cascade = CascadeType.REMOVE)
	List<BillingItemType> items;
	
	LocalDate currentMonth;

}
