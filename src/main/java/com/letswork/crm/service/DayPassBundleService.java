package com.letswork.crm.service;

import java.util.List;

import com.letswork.crm.entities.DayPassBundle;

public interface DayPassBundleService {
	
	DayPassBundle saveOrUpdate(DayPassBundle bundle);

    List<DayPassBundle> getAllByCompanyId(String companyId);

}
