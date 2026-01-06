package com.letswork.crm.entities;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "conference_room_time_slots",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {
                "company_id",
                "lets_work_centre",
                "city",
                "state",
                "room_name",
                "slot_date",
                "start_time"
            }
        )
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceRoomTimeSlot extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String letsWorkCentre;
    
    private String city;
    
    private String state;

    private String roomName;

    private LocalDate slotDate;

    private LocalTime startTime;

    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private BookConferenceRoom booking;
    
}
