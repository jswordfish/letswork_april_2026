package com.letswork.crm.dtos;

import com.poiji.annotation.ExcelCellName;

import lombok.Data;

@Data
public class ParkingSlotExcelDto {
	
	@ExcelCellName("Name")
    private String name;

    @ExcelCellName("LetsWork Centre")
    private String letsWorkCentre;

    @ExcelCellName("Floor Number")
    private String floorNumber;

    @ExcelCellName("Other Details")
    private String otherDetails;
    
    @ExcelCellName("Company Id")
    private String companyId;

}
