package com.letswork.crm.dtos;

import com.poiji.annotation.ExcelCellName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientExcelDto {
	
	@ExcelCellName("Name")
    private String name;

    @ExcelCellName("Email")
    private String email;

    @ExcelCellName("Phone")
    private String phone;

    @ExcelCellName("Client Company Name")
    private String clientCompanyName;

    @ExcelCellName("LetsWork Centre")
    private String letsWorkCentre;
    
    @ExcelCellName("Business Category")
    private String businessCategory;

    @ExcelCellName("Company Id")
    private String companyId;
    
    
}
