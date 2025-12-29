package com.letswork.crm.service;

import java.util.List;

import com.letswork.crm.entities.ConferenceBundle;

public interface ConferenceBundleService {
	
	ConferenceBundle saveOrUpdate(ConferenceBundle bundle);

    List<ConferenceBundle> getAllByCompanyId(String companyId);

}
