package com.letswork.crm.dtos;

import com.poiji.annotation.ExcelCellName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientExcelDto {
	
	@ExcelCellName("First Name")
    private String firstName;
	
	@ExcelCellName("Last Name")
    private String lastName;

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
    
    @ExcelCellName("State")
    private String state;
    
    @ExcelCellName("City")
    private String city;
    
}
