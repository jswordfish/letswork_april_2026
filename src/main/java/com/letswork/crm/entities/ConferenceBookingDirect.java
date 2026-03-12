package com.letswork.crm.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("Conference")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceBookingDirect extends Booking{
	
	private LocalDateTime purchaseDate;
	
  @ManyToOne	
  LetsWorkCentre letsWorkCentre;

  @OneToMany(mappedBy = "booking",
          cascade = CascadeType.ALL,
          orphanRemoval = true)
  private List<ConferenceRoomTimeSlot> slots = new ArrayList<>();
  
  @ManyToOne
  ConferenceRoom conferenceRoom;
  
  @ManyToOne
  Offers appliedOffer;
  
  Float price;
  
  Float discountedPrice;

}
