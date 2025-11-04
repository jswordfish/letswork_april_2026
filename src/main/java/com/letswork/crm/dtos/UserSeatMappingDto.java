package com.letswork.crm.dtos;



import com.letswork.crm.enums.SeatType;

import lombok.Data;

@Data
public class UserSeatMappingDto {
	
	
	
	public UserSeatMappingDto(String letsWorkCentre, SeatType seatType, String seatNumber, String city, String state) {
		super();
		this.letsWorkCentre = letsWorkCentre;
		this.seatType = seatType;
		this.seatNumber = seatNumber;
		this.city = city;
		this.state = state;
	}

	private String email;
	
	private String letsWorkCentre;
	
	private SeatType seatType;
		
	private String seatNumber;
	
	private String city;
	
	private String state;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserSeatMappingDto other = (UserSeatMappingDto) obj;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (letsWorkCentre == null) {
			if (other.letsWorkCentre != null)
				return false;
		} else if (!letsWorkCentre.equals(other.letsWorkCentre))
			return false;
		if (seatNumber == null) {
			if (other.seatNumber != null)
				return false;
		} else if (!seatNumber.equals(other.seatNumber))
			return false;
		if (seatType != other.seatType)
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((letsWorkCentre == null) ? 0 : letsWorkCentre.hashCode());
		result = prime * result + ((seatNumber == null) ? 0 : seatNumber.hashCode());
		result = prime * result + ((seatType == null) ? 0 : seatType.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}
	
	

}
