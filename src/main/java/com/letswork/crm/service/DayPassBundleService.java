package com.letswork.crm.service;

import java.util.List;

import com.letswork.crm.entities.DayPassBundle;

public interface DayPassBundleService {
	
	DayPassBundle saveOrUpdate(DayPassBundle bundle);

    List<DayPassBundle> getAllByCompanyId(String companyId);
    
    List<DayPassBundle> getByCentres(String letsWorkCentre,
            String companyId,
            String city,
            String state
            );
    
    public List<DayPassBundle> searchBundles(
	        String companyId,
	        String letsWorkCentre,
	        String city,
	        String state,
	        Integer numberOfDays,
	        Integer validForDays,
	        Integer discountPercentage,
	        float price,
	        String search
	);

}
