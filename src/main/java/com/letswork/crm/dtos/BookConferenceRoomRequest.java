package com.letswork.crm.dtos;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.letswork.crm.entities.Booking;

import lombok.Data;

@Data
public class BookConferenceRoomRequest {

    private Booking booking;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate slotDate;

    private List<ConferenceRoomSlotRequest> slots;
    
}
