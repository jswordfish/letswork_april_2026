package com.letswork.crm.dtos;

import com.poiji.annotation.ExcelCellName;

import lombok.Data;

@Data
public class ConferenceRoomExcelDto {
	
	@ExcelCellName("Name")
    private String name;

    @ExcelCellName("Capacity")
    private Integer capacity;

    @ExcelCellName("LetsWork Centre")
    private String letsWorkCentre;
    
    @ExcelCellName("Company Id")
    private String companyId;
    
    @ExcelCellName("Has Projector")
    private Boolean hasProjector;
    
    @ExcelCellName("Has White Board")
    private Boolean hasWhiteBoard;
    
    @ExcelCellName("Has Charging Ports")
    private Boolean hasChargingPorts;
    
    @ExcelCellName("State")
    private String state;
    
    @ExcelCellName("City")
    private String city;

}
