package com.letswork.crm.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Builder
public class LetsWorkClientPurchesedDto {

	private Long clientId;
	private Integer purchasedDayPassCredits;
	private Float purchasedConferenceCredits;
//	private String companyId;
}
