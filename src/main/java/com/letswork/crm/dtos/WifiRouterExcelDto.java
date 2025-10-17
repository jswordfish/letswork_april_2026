package com.letswork.crm.dtos;

import com.poiji.annotation.ExcelCellName;

import lombok.Data;

@Data
public class WifiRouterExcelDto {
	
	@ExcelCellName("LetsWork Centre")
    private String letsWorkCentre;

    @ExcelCellName("Wifi Name")
    private String wifiName;

    @ExcelCellName("Password")
    private String password;
    
    @ExcelCellName("Company Id")
    private String companyId;
    
    @ExcelCellName("State")
    private String state;
    
    @ExcelCellName("City")
    private String city;

}
