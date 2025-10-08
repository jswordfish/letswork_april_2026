package com.LetsWork.CRM.entities;

import java.time.LocalDateTime;

import com.LetsWork.CRM.enums.BookingStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Booking {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String companyId;

    private String clientName;
    
    private String clientEmail;
    
    private String clientCompany;
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientCompany == null) ? 0 : clientCompany.hashCode());
		result = prime * result + ((clientEmail == null) ? 0 : clientEmail.hashCode());
		result = prime * result + ((companyId == null) ? 0 : companyId.hashCode());
		result = prime * result + ((conferenceRoomName == null) ? 0 : conferenceRoomName.hashCode());
		result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Booking other = (Booking) obj;
		if (clientCompany == null) {
			if (other.clientCompany != null)
				return false;
		} else if (!clientCompany.equals(other.clientCompany))
			return false;
		if (clientEmail == null) {
			if (other.clientEmail != null)
				return false;
		} else if (!clientEmail.equals(other.clientEmail))
			return false;
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
			return false;
		if (conferenceRoomName == null) {
			if (other.conferenceRoomName != null)
				return false;
		} else if (!conferenceRoomName.equals(other.conferenceRoomName))
			return false;
		if (endTime == null) {
			if (other.endTime != null)
				return false;
		} else if (!endTime.equals(other.endTime))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (startTime == null) {
			if (other.startTime != null)
				return false;
		} else if (!startTime.equals(other.startTime))
			return false;
		return true;
	}
	

    private String conferenceRoomName;
    
    private String location;  

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String qrCodePath; // Store QR code image path
    private String bookingCode; // Unique code for verification

    
    private boolean isActive = true;
    
    @Enumerated(EnumType.STRING)  
    @Column(nullable = false)
    private BookingStatus currentStatus;

    private String s3Path;
    
    
}
