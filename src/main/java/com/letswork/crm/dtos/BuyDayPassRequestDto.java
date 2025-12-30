package com.letswork.crm.dtos;

import lombok.Data;

@Data
public class BuyDayPassRequestDto {

    private String email;
    
    private Long bundleId;
    
    private String companyId;
    
}
