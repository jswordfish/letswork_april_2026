package com.LetsWork.CRM.dtos;

import com.poiji.annotation.ExcelCellName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationExcelDto {
	
	
	@ExcelCellName("Name")
    private String name;

    @ExcelCellName("TotalSeats")
    private Integer totalSeats;

    @ExcelCellName("TotalConferenceRooms")
    private Integer totalConferenceRooms;

    @ExcelCellName("Address")
    private String address;

    @ExcelCellName("CompanyId")
    private String companyId;

}
