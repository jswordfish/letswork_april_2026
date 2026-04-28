package com.letswork.crm.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.letswork.crm.enums.BookedFrom;
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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="booking_type", 
  discriminatorType = DiscriminatorType.STRING)
public class Booking extends Base{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne
	LetsWorkClient letsWorkClient;
	
	private float frontendAmount;
	
	private Integer frontendDiscountPercentage;
	
	private float frontendDiscountedAmount;
	
	private Integer frontendCgstPercentage;
	
	private Integer frontendSgstPercentage;
	
	private float frontendFinalAmountAfterAddingTax;

    private BigDecimal amount;

    private LocalDateTime dateOfPurchase;

    private LocalDate startDate;
    
    private String referenceId;
    
    private String razorpayOrderId;
    
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    private BookedFrom bookedFrom;
    
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;
    
    @Transient
    @JsonIgnoreProperties("booking")
    Invoice invoice;
    
    @JsonProperty("bookingType")
    public String getBookingType() {
        return this.getClass().getSimpleName();
    }
    
    public void setDateOfPurchase(LocalDateTime dateOfPurchase) {
        if (dateOfPurchase != null) {
            LocalDate inputDate = dateOfPurchase.toLocalDate();
            LocalDate today = LocalDate.now();

            if (inputDate.isBefore(today)) {
                throw new IllegalArgumentException("Purchase date cannot be in the past.");
            }
        }
        this.dateOfPurchase = dateOfPurchase;
    }
    
}
