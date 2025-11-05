package com.letswork.crm.dtos;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.letswork.crm.enums.SeatType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatMappingResponseDto {

    private Long id;
    private String name; 
    private String letsWorkCentre;
    private SeatType seatType;
    private String seatNumber;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate startDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate endDate;

    private String city;
    private String state;
    private String mappingType;
}
