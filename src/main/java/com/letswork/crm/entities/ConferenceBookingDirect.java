package com.letswork.crm.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("ConferenceBookingDirect")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceBookingDirect extends Booking{
	
	
  @ManyToOne	
  LetsWorkCentre letsWorkCentre;

  @OneToMany(fetch = FetchType.EAGER,
		  mappedBy = "booking",
          cascade = CascadeType.ALL,
          orphanRemoval = true)
  @JsonManagedReference
  private List<ConferenceRoomTimeSlot> slots = new ArrayList<>();
  
  @ManyToOne
  ConferenceRoom conferenceRoom;
  
  @ManyToOne
  Offers appliedOffer;
  
  BigDecimal price;
  
  BigDecimal discountedPrice;
  
  String qrS3Path;

}
