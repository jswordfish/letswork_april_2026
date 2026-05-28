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
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("ConferenceBookingDirect")
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceBookingDirect extends Booking{
	
	
  
  
//  @Transient
//  private List<BundleBookingCreditMapper> multipleBundleList;

  @OneToMany(fetch = FetchType.EAGER,
		  mappedBy = "booking",
          cascade = CascadeType.ALL,
          orphanRemoval = true)
  @JsonManagedReference
  private List<ConferenceRoomTimeSlot> slots = new ArrayList<>();
  
//  @Lob
//  private String multipleBundleListJson;
  
  @ManyToOne
  ConferenceRoom conferenceRoom;
  
  @ManyToOne
  Offers appliedOffer;
  
  BigDecimal price;
  
  BigDecimal discountedPrice;
  
  String qrS3Path;

  

//  public List<BundleBookingCreditMapper> getMultipleBundleList() {
//	  if(this.getMultipleBundleListJson() != null) {
//		  String json = this.getMultipleBundleListJson();
//		  try {
//			  this.multipleBundleList = mapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<List<BundleBookingCreditMapper>>() {});
//		  }
//		  catch (Exception e) {
//			  return multipleBundleList;
//		  }
//	  }
//	return multipleBundleList;
//  }
//
//  public void setMultipleBundleList(List<BundleBookingCreditMapper> multipleBundleList) {
//	this.multipleBundleList = multipleBundleList;
//	 try {
//			this.multipleBundleListJson =  mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.multipleBundleList);
//		  } catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//		  }
//	
//  }

  public List<ConferenceRoomTimeSlot> getSlots() {
	return slots;
  }

  public void setSlots(List<ConferenceRoomTimeSlot> slots) {
	this.slots = slots;
  }
  
//  ObjectMapper mapper = new ObjectMapper();
//
//  public String getMultipleBundleListJson() {
//	  if(this.multipleBundleListJson != null) {
//		  return this.multipleBundleListJson;
//	  }
//	  if(this.multipleBundleList != null) {
//		  try {
//			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this.multipleBundleList);
//		  } catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			return multipleBundleListJson;
//		  }
//	  }
//	return multipleBundleListJson;
//  }
//
//  public void setMultipleBundleListJson(String multipleBundleListJson) {
//	this.multipleBundleListJson = multipleBundleListJson;
//  }

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
