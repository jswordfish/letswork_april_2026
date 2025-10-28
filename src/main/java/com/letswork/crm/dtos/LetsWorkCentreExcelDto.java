package com.letswork.crm.dtos;

import java.time.LocalTime;

import com.poiji.annotation.ExcelCellName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LetsWorkCentreExcelDto {
	
	
	@ExcelCellName("Name")
    private String name;

    @ExcelCellName("State")
    private String state;
    
    @ExcelCellName("City")
    private String city;

    @ExcelCellName("Total Conference Rooms")
    private Integer totalConferenceRooms;

    @ExcelCellName("Address")
    private String address;
    
    @ExcelCellName("Has Cafe")
    private Boolean hasCafe;
    
    @ExcelCellName("Amenities")
    private String amenities;

    @ExcelCellName("Company Id")
    private String companyId;
    
    @ExcelCellName("Start Time Weekdays")
    private String startTimeRegular;
    
    @ExcelCellName("End Time Weekdays")
    private String endTimeRegular;
    
    @ExcelCellName("Start Time Saturday")
    private String startTimeSat;
    
    @ExcelCellName("End Time Sturday")
    private String endTimeSat;
    
    @ExcelCellName("Latitude")
    private String latitude;
    
    @ExcelCellName("Longitude")
    private String longitude;

}
