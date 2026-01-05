package com.letswork.crm.dtos;

import lombok.Data;

@Data
public class BuyConferenceBundleRequestDto {

    private String companyId;
    
    private String email;
    
    private Long bundleId;
    
}
