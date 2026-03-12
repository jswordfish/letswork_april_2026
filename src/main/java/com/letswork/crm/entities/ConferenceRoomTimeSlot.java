package com.letswork.crm.entities;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceRoomTimeSlot extends Base{

 

    private LocalDate slotDate;

    private LocalTime startTime;

    private LocalTime endTime;
    
    
    
}
