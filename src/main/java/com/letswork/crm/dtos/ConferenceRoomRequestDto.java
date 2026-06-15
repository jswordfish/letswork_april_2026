package com.letswork.crm.dtos;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class ConferenceRoomRequestDto {
	
		private Long id;

	    private String companyId;

	    private String name;

	    private Integer capacity;

	    private String letsWorkCentre;

	    private BigDecimal halfHourPrice;

	    private String state;

	    private String city;

	    private List<Long> amenityIds;

}
