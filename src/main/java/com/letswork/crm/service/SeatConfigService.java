package com.letswork.crm.service;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.SeatConfig;
import com.letswork.crm.enums.SeatType;

public interface SeatConfigService {
	
	SeatConfig saveOrUpdate(SeatConfig seatConfig);

	public PaginatedResponseDto listSeatConfigs(
            String companyId,
            String letsWorkCentre,
            String city,
            String state,
            SeatType seatType,
            int page,
            int size
    );

}
