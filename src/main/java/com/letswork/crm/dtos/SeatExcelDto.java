package com.letswork.crm.dtos;

import com.poiji.annotation.ExcelCellName;

import lombok.Data;

@Data
public class SeatExcelDto {
	
	@ExcelCellName("LetsWork Centre")
    private String letsWorkCentre;

    @ExcelCellName("Seat Type")
    private String seatType;

    @ExcelCellName("Seat Number")
    private String seatNumber;

    @ExcelCellName("Cost Per Day")
    private Integer costPerDay;

    @ExcelCellName("Cost Per Month")
    private Integer costPerMonth;

    @ExcelCellName("Cabin Name")
    private String cabinName;

    @ExcelCellName("Company Id")
    private String companyId;

}
