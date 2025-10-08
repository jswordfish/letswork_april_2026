package com.LetsWork.CRM.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.LetsWork.CRM.entities.CreditConferenceRoomMapping;

@Repository
public interface CreditConferenceRoomMappingRepository extends JpaRepository<CreditConferenceRoomMapping, Long>{
	
	Page<CreditConferenceRoomMapping> findByCompanyId(String companyId, Pageable pageable);
	
	CreditConferenceRoomMapping findByConferenceRoomNameAndLocationAndCompanyId(String roomName, String location, String companyId);

}
