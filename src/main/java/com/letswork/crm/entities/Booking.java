package com.letswork.crm.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letswork.crm.dtos.BundleBookingCreditMapper;
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
  discriminatorType = DiscriminatorType.STRING,
  length = 255)
public class Booking extends Base{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne
	LetsWorkClient letsWorkClient;
	
	private Long bookedByUserId;
	
	@ManyToOne
    @JoinColumn(name = "booked_by_user")
	private NewUserRegister bookedByUser;
	
	@ManyToOne
	@JoinColumn(name = "lets_work_centre_id")
	private LetsWorkCentre letsWorkCentre;
	
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
    
 // 1. Map the discriminator column as a read-only string field for JPQL/Criteria queries
    @Column(name = "booking_type", insertable = false, updatable = false)
    private String bookingType;
    
  @Transient
  private List<BundleBookingCreditMapper> multipleBundleList;
  
  @Transient
  @JsonIgnore
  ObjectMapper mapper = new ObjectMapper();
  
	@Lob
	private String multipleBundleListJson;
	
  

  public String getMultipleBundleListJson() {
	  if(this.multipleBundleListJson != null) {
		  return this.multipleBundleListJson;
	  }
	  if(this.multipleBundleList != null) {
		  try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.multipleBundleList);
		  } catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return multipleBundleListJson;
		  }
	  }
	return multipleBundleListJson;
  }

  public void setMultipleBundleListJson(String multipleBundleListJson) {
	this.multipleBundleListJson = multipleBundleListJson;
  }
	
  public List<BundleBookingCreditMapper> getMultipleBundleList() {
	  if(this.getMultipleBundleListJson() != null) {
		  String json = this.getMultipleBundleListJson();
		  try {
			  this.multipleBundleList = mapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<List<BundleBookingCreditMapper>>() {});
		  }
		  catch (Exception e) {
			  return multipleBundleList;
		  }
	  }
	return multipleBundleList;
}

public void setMultipleBundleList(List<BundleBookingCreditMapper> multipleBundleList) {
	this.multipleBundleList = multipleBundleList;
	 try {
			this.multipleBundleListJson =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.multipleBundleList);
		  } catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
		  }
	
}
    
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