package com.letswork.crm.dtos;

import lombok.Data;

@Data
public class AmenityDto {
	
    private String name;
    
    private String description;

    public AmenityDto() {}

    public AmenityDto(String name, String description) {
        this.name = name;
        this.description = description;
    }

}
