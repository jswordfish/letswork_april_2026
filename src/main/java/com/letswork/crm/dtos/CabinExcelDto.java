package com.letswork.crm.dtos;

import com.poiji.annotation.ExcelCellName;

import lombok.Data;

@Data
public class CabinExcelDto {
	
	@ExcelCellName("Cabin Name")
    private String cabinName;

    @ExcelCellName("Location")
    private String location;

    @ExcelCellName("Cabin Number")
    private String cabinNumber;

    @ExcelCellName("Total Seats")
    private int totalSeats;
    
    @ExcelCellName("Company Id")
    private String companyId;

}
