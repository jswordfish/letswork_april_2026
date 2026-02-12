package com.letswork.crm.service;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.SeatConfig;

public interface SeatConfigService {
	
	SeatConfig saveOrUpdate(SeatConfig seatConfig);

    PaginatedResponseDto listSeatConfigs(String companyId, int page, int size);

}
