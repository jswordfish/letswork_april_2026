package com.letswork.crm.dtos;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ConferenceRoomBundleBookingRequest {
	
	private Long bundleBookingId;
	private Long clientId;

    private String companyId;
    private String centre;
    private String city;
    private String state;
    private String roomName;

    private LocalDate slotDate;

    private List<ConferenceRoomSlotRequest> slots;

}
