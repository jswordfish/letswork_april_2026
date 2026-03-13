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
@DiscriminatorValue("ConferenceRoomBookingThroughBundle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceRoomBookingThroughBundle extends Booking{
	
	private LocalDateTime purchaseDate;
	
	//
	@ManyToOne
	ConferenceBundle bundle;

	/*
    CONFERENCE ROOM FIELDS
    */
	//mandatory
   @ManyToOne
   ConferenceRoom conferenceRoom;
   
   @ManyToOne
   LetsWorkCentre letsWorkCentre;

   private Integer numberOfGuests;

   private Float numberOfHours;
   
   @ManyToOne
   ConferenceBundle conferenceBundle;
   
  
   /*
   SLOT MAPPING
   */

  @OneToMany(
          cascade = CascadeType.ALL,
          orphanRemoval = true)
  private List<ConferenceRoomTimeSlot> slots = new ArrayList<>();

}
