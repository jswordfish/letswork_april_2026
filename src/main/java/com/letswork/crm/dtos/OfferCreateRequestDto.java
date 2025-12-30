package com.letswork.crm.dtos;

import java.time.LocalDateTime;
import java.util.List;

import com.letswork.crm.entities.LetsWorkCentre;

import lombok.Data;

@Data
public class OfferCreateRequestDto {

    private String name;
    private String code;
    private String discount;
    private String minDiscountValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private List<Long> centreIds;
    
    private List<LetsWorkCentre> letsWorkCentres;

    private String companyId;
    
}
