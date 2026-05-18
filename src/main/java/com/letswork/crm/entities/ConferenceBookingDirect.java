package com.letswork.crm.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letswork.crm.dtos.BundleBookingCreditMapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("ConferenceBookingDirect")
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

  public LetsWorkCentre getLetsWorkCentre() {
	return letsWorkCentre;
  }

  public void setLetsWorkCentre(LetsWorkCentre letsWorkCentre) {
	this.letsWorkCentre = letsWorkCentre;
  }



  public List<ConferenceRoomTimeSlot> getSlots() {
	return slots;
  }

  public void setSlots(List<ConferenceRoomTimeSlot> slots) {
	this.slots = slots;
  }
  


  public ConferenceRoom getConferenceRoom() {
	return conferenceRoom;
  }

  public void setConferenceRoom(ConferenceRoom conferenceRoom) {
	this.conferenceRoom = conferenceRoom;
  }

  public Offers getAppliedOffer() {
	return appliedOffer;
  }

  public void setAppliedOffer(Offers appliedOffer) {
	this.appliedOffer = appliedOffer;
  }

  public BigDecimal getPrice() {
	return price;
  }

  public void setPrice(BigDecimal price) {
	this.price = price;
  }

  public BigDecimal getDiscountedPrice() {
	return discountedPrice;
  }

  public void setDiscountedPrice(BigDecimal discountedPrice) {
	this.discountedPrice = discountedPrice;
  }

  public String getQrS3Path() {
	return qrS3Path;
  }

  public void setQrS3Path(String qrS3Path) {
	this.qrS3Path = qrS3Path;
  }
  
  

}
