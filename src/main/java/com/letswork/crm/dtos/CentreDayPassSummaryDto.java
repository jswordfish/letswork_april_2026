package com.letswork.crm.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CentreDayPassSummaryDto {

    private String letsWorkCentre;
    
    private String city;
    
    private String state;
    
    private Integer totalDayPassCredits;
    
}
