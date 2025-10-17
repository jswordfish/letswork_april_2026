package com.letswork.crm.dtos;

import com.poiji.annotation.ExcelCellName;

import lombok.Data;

@Data
public class ClientCompanyExcelDto {
	
	@ExcelCellName("Client Company Name")
    private String clientCompanyName;

    @ExcelCellName("Industry")
    private String industry;

    @ExcelCellName("LetsWork Centre")
    private String letsWorkCentre;
    
    @ExcelCellName("Company Id")
    private String companyId;
    
    @ExcelCellName("State")
    private String state;
    
    @ExcelCellName("City")
    private String city;

}
