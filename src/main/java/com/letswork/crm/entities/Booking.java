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

    private BigDecimal amount;

    private LocalDateTime dateOfPurchase;

    private LocalDate startDate;
    
    private String referenceId;

    @Enumerated(EnumType.STRING)
    private BookedFrom bookedFrom;
    
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;
    
}
