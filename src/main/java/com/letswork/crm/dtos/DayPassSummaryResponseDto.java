package com.letswork.crm.dtos;

import java.util.List;

import lombok.Data;

@Data
public class DayPassSummaryResponseDto {

    private Integer totalDayPassCredits;
    
    private List<CentreDayPassSummaryDto> letsWorkCentreCredit;
    
}
