package com.letswork.crm.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.letswork.crm.enums.PrinterType;

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
public class Printer extends Base{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String printerName;
	
	private String letsWorkCentre;
	
	@Enumerated(EnumType.STRING)  
    @Column(nullable = false)
    private PrinterType printerType;
	
	private String printerCompany;
	
	private String state;
	
	private String city;

}
