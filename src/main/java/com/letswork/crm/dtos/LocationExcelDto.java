package com.letswork.crm.dtos;

import com.poiji.annotation.ExcelCellName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationExcelDto {
	
	
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
    private boolean hasCafe;
    
    @ExcelCellName("Amenities")
    private String amenities;

    @ExcelCellName("Company Id")
    private String companyId;

}
