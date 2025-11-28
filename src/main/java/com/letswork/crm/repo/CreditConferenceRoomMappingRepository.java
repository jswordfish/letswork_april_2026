package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.CreditConferenceRoomMapping;

@Repository
public interface CreditConferenceRoomMappingRepository extends JpaRepository<CreditConferenceRoomMapping, Long>{
	
	Page<CreditConferenceRoomMapping> findByCompanyId(String companyId, Pageable pageable);
	
	List<CreditConferenceRoomMapping> findByConferenceRoomNameAndLetsWorkCentreAndCompanyIdAndCityAndState(String roomName, String letsWorkCentre, String companyId, String city, String state);

}
