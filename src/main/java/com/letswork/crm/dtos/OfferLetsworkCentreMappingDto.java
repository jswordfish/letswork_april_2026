package com.letswork.crm.dtos;

import java.util.List;

import lombok.Data;

@Data
public class OfferLetsworkCentreMappingDto {
	
	String offerName;
	
	List<Long> centerIds;
	
	String companyId;

}
