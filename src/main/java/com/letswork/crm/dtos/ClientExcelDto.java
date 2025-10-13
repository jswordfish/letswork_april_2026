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

    @ExcelCellName("IsIndividual")
    private Boolean isIndividual;

    @ExcelCellName("ClientCompanyName")
    private String clientCompanyName;

    @ExcelCellName("Location")
    private String location;

    @ExcelCellName("CompanyId")
    private String companyId;

}
