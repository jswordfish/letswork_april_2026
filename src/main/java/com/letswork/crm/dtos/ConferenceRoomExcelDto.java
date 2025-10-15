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
    private boolean hasProjector;
    
    @ExcelCellName("Has White Board")
    private boolean hasWhiteBoard;
    
    @ExcelCellName("Has Charging Ports")
    private boolean hasChargingPorts;

}
