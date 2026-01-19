package com.letswork.crm.service;

import java.time.LocalDate;

import org.springframework.data.domain.Pageable;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Referral;

public interface ReferralService {
	
	Referral saveOrUpdate(Referral referral);

	PaginatedResponseDto getPaginated(
	        String companyId,
	        String email,
	        String name,
	        String emailOfUser,
	        LocalDate fromDate,
	        LocalDate toDate,
	        int page,
	        int size
	);

    Referral updateJoiningDate(
            Long referralId,
            LocalDate joiningDate,
            String companyId
    );

    boolean isEligibleForBonus(Long referralId, String companyId);

    void giveBonus(Long referralId, String companyId);

}
