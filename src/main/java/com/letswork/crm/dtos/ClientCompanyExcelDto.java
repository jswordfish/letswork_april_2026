package com.letswork.crm.dtos;

import com.poiji.annotation.ExcelCellName;

import lombok.Data;

@Data
public class ClientCompanyExcelDto {
	
	@ExcelCellName("ClientCompanyName")
    private String clientCompanyName;

    @ExcelCellName("Industry")
    private String industry;

    @ExcelCellName("Location")
    private String location;

}
