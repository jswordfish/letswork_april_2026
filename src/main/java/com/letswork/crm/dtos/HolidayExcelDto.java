package com.letswork.crm.dtos;

import java.time.LocalDate;

import com.poiji.annotation.ExcelCellName;

import lombok.Data;

@Data
public class HolidayExcelDto {
	
	@ExcelCellName("LetsWork Centre")
    private String letsWorkCentre;

    @ExcelCellName("Company Id")
    private String companyId;

    @ExcelCellName("City")
    private String city;

    @ExcelCellName("State")
    private String state;

    @ExcelCellName("Holiday Date")
    private LocalDate holidayDate;

    @ExcelCellName("Holiday Reason")
    private String holidayReason;

}
