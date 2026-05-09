package com.letswork.crm.dtos;

import java.time.LocalDateTime;
import java.util.Date;

import com.letswork.crm.enums.EnquiryType;

import lombok.Data;

@Data
public class EnquiryDto {
	
	private String name;

    private String email;

    private String phoneNumber;

    private String description;

    private String letsWorkCentre;

    private String city;

    private String state;

    private EnquiryType enquiryType;

    private Long solutionId;
    
    private String solutionName;
    
    private String companyId;
    
    private Date date;

    private LocalDateTime time;

}
