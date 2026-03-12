package com.letswork.crm.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

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
public class BookingInvoiceMapping extends Base{
	
	@ManyToOne
	Booking booking;
	
	@ManyToOne
	Invoice invoice;
	
	

}
