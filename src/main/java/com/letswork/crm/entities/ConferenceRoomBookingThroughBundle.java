package com.letswork.crm.entities;

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
@DiscriminatorValue("ConferenceRoomBookingThroughBundle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceRoomBookingThroughBundle extends Booking{
	
	
	
	@ManyToOne
	private ConferenceBundleBooking bundleBooking;

	
	//mandatory
   @ManyToOne
   ConferenceRoom conferenceRoom;
   
   @ManyToOne
   LetsWorkCentre letsWorkCentre;

   private Integer numberOfGuests;

   private Float numberOfHours;
   
   private String qrS3Path;
   
   private Long previousBookingId;
   
//   @ManyToOne
//   ConferenceBundle conferenceBundle;
   
  
   /*
   SLOT MAPPING
   */

  @OneToMany(fetch = FetchType.EAGER,
		  mappedBy = "booking",
          cascade = CascadeType.ALL,
          orphanRemoval = true)
  @JsonManagedReference
  private List<ConferenceRoomTimeSlot> slots = new ArrayList<>();

}
