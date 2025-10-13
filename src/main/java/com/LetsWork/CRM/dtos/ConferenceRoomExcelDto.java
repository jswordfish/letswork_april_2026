package com.LetsWork.CRM.dtos;

import com.poiji.annotation.ExcelCellName;

import lombok.Data;

@Data
public class ConferenceRoomExcelDto {
	
	@ExcelCellName("Name")
    private String name;

    @ExcelCellName("Capacity")
    private Integer capacity;

    @ExcelCellName("Location")
    private String location;

    @ExcelCellName("RoomType")
    private String roomType;

}
