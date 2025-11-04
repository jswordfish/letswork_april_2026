package com.letswork.crm.entities;

import com.letswork.crm.enums.SeatType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SeatKey {
    private String letsWorkCentre;
    private String city;
    private String state;
    private String companyId;
    private SeatType seatType;
    private String seatNumber;
}
