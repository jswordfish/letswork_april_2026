package com.LetsWork.CRM.entities;

import java.time.LocalDateTime;

import com.LetsWork.CRM.enums.BookingStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Credit extends Base {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String itemUnit;
	
	private String itemQuantity;
	
	private int numberOfCredits;
	
	private int pricePerCredit;
	
	private String currency;
	
	private String creditName;

}
