package com.letswork.crm.dtos;

import com.poiji.annotation.ExcelCellName;

import lombok.Data;

@Data
public class PrinterExcelDto {
	
	@ExcelCellName("Printer Name")
    private String printerName;

    @ExcelCellName("LetsWork Centre")
    private String letsWorkCentre;

    @ExcelCellName("Printer Type")
    private String printerType;

    @ExcelCellName("Printer Company")
    private String printerCompany;
    
    @ExcelCellName("Company Id")
    private String companyId;
    
    @ExcelCellName("State")
    private String state;
    
    @ExcelCellName("City")
    private String city;

}
