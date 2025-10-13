package com.LetsWork.CRM.dtos;

import com.poiji.annotation.ExcelCellName;

import lombok.Data;

@Data
public class WifiRouterExcelDto {
	
	@ExcelCellName("Location")
    private String location;

    @ExcelCellName("WifiName")
    private String wifiName;

    @ExcelCellName("Password")
    private String password;

}
