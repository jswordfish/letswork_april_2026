package com.letswork.crm.entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.letswork.crm.enums.BookingType;
import com.letswork.crm.enums.InvoiceStatus;

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
public class Invoice extends Base{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String companyEmail;
	
	private Integer amount;
	
	@Enumerated(EnumType.STRING)
    private BookingType bookingType;
	
	private Long bookingId;
	
	@Enumerated(EnumType.STRING)
    private InvoiceStatus invoiceStatus;
	
	private String pdfS3KeyName;
	

}
