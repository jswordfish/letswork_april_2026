package com.LetsWork.CRM.dtos;

import com.poiji.annotation.ExcelCellName;

import lombok.Data;

@Data
public class ParkingSlotExcelDto {
	
	@ExcelCellName("Name")
    private String name;

    @ExcelCellName("Location")
    private String location;

    @ExcelCellName("FloorNumber")
    private String floorNumber;

    @ExcelCellName("OtherDetails")
    private String otherDetails;

}
