package com.letswork.crm.entities;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

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
public class Payment extends Base{
	
	String paymentId;
	
	String bankTransactioId;
	
	BigDecimal amount;
	
	Float cgst;
	
	Float sgst;
	
	Float igst;
	
	PaymentStatus paymentStatus;
	
	String description;
	
	@Transient
	Long letsworkClientId;
	
	@ManyToOne
	LetsWorkClient letsWorkClient;
	
	@ManyToOne
	Invoice invoice;
	
	
}
