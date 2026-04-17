package com.letswork.crm.dtos;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ConferenceBookingDirectRequest {
	
	private Long clientId;

    private String companyId;
    private String centre;
    private String city;
    private String state;
    private String roomName;

    private LocalDate slotDate;

    private List<ConferenceRoomSlotRequest> slots;

    private Long offerId;
    
	private float frontendAmount;
	
	private Integer frontendDiscountPercentage;
	
	private float frontendDiscountedAmount;
	
	private Integer frontendCgstPercentage;
	
	private Integer frontendSgstPercentage;
	
	private float frontendFinalAmountAfterAddingTax;

}
