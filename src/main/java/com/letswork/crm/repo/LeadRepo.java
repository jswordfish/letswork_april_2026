package com.letswork.crm.repo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Lead;
import com.letswork.crm.enums.LeadQuality;
import com.letswork.crm.enums.LeadStatus;
import com.letswork.crm.enums.Source;

@Repository
public interface LeadRepo extends JpaRepository<Lead, Long>{
	
	Lead findByEmailAndCompanyId(String email, String companyId);

	Lead findByPhoneAndCompanyId(String phone, String companyId);

	Lead findByIdAndCompanyId(Long id, String companyId);
	
	Optional<Lead> findFirstByEmailAndCompanyIdOrderByIdDesc(String email, String companyId);
	
	@Query( "SELECT l FROM Lead l " + "WHERE l.companyId = :companyId " + "AND (:name IS NULL OR l.name = :name) " + "AND (:email IS NULL OR l.email = :email) " + "AND (:phone IS NULL OR l.phone = :phone) " + "AND (:clientCompanyName IS NULL OR l.clientCompanyName = :clientCompanyName) " + "AND (:source IS NULL OR l.source = :source) " + "AND (:location IS NULL OR l.location = :location) " + "AND (:status IS NULL OR l.status = :status) " + "AND (:leadQuality IS NULL OR l.leadQuality = :leadQuality) " + "AND (:letsWorkCentre IS NULL OR l.letsWorkCentre = :letsWorkCentre) " + "AND (:city IS NULL OR l.city = :city) " + "AND (:state IS NULL OR l.state = :state) " + "AND (:solution IS NULL OR l.solution = :solution) " + "AND (:fromDate IS NULL OR l.createDate >= :fromDate) " + "AND (:toDate IS NULL OR l.createDate <= :toDate) " + "AND (" + ":search IS NULL OR " + "LOWER(l.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " + "LOWER(l.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " + "LOWER(l.phone) LIKE LOWER(CONCAT('%', :search, '%')) OR " + "LOWER(l.clientCompanyName) LIKE LOWER(CONCAT('%', :search, '%')) OR " + "LOWER(l.location) LIKE LOWER(CONCAT('%', :search, '%')) OR " + "LOWER(l.letsWorkCentre) LIKE LOWER(CONCAT('%', :search, '%')) OR " + "LOWER(l.city) LIKE LOWER(CONCAT('%', :search, '%')) OR " + "LOWER(l.state) LIKE LOWER(CONCAT('%', :search, '%')) OR " + "LOWER(l.solution) LIKE LOWER(CONCAT('%', :search, '%'))" + ")" ) Page<Lead> filterNormal( String companyId, String name, String email, String phone, String clientCompanyName, Source source, String location, LeadStatus status, LeadQuality leadQuality, String letsWorkCentre, String city, String state, String solution, String search, Date fromDate, Date toDate, Pageable pageable );
	
	@Query(
		    "SELECT l FROM Lead l " +
		    "WHERE l.companyId = :companyId " +
		    "AND (" +
		        ":userId IS NULL OR " +
		        "l.id IN (" +
		            "SELECT al.leadId FROM AssignLead al WHERE al.userId = :userId" +
		        ")" +
		    ") " +
		    "AND (:name IS NULL OR l.name = :name) " +
		    "AND (:email IS NULL OR l.email = :email) " +
		    "AND (:phone IS NULL OR l.phone = :phone) " +
		    "AND (:clientCompanyName IS NULL OR l.clientCompanyName = :clientCompanyName) " +

		    "AND (:checkSources = false OR l.source IN :sources) " +
		    "AND (:location IS NULL OR l.location = :location) " +
		    "AND (:checkStatuses = false OR l.status IN :statuses) " +
		    "AND (:checkLeadQualities = false OR l.leadQuality IN :leadQualities) " +
		    "AND (:checkLetsWorkCentres = false OR l.letsWorkCentre IN :letsWorkCentres) " +

		    "AND (:city IS NULL OR l.city = :city) " +
		    "AND (:state IS NULL OR l.state = :state) " +
		    "AND (:solution IS NULL OR l.solution = :solution) " +
		    "AND (:fromDate IS NULL OR l.createDate >= :fromDate) " +
		    "AND (:toDate IS NULL OR l.createDate <= :toDate) " +
		    "AND (" +
		        ":search IS NULL OR " +
		        "LOWER(l.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		        "LOWER(l.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		        "LOWER(l.phone) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		        "LOWER(l.clientCompanyName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		        "LOWER(l.location) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		        "LOWER(l.letsWorkCentre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		        "LOWER(l.city) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		        "LOWER(l.state) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		        "LOWER(l.solution) LIKE LOWER(CONCAT('%', :search, '%'))" +
		    ")"
		)
		Page<Lead> filter(
		        String companyId,
		        Long userId,
		        String name,
		        String email,
		        String phone,
		        String clientCompanyName,

		        boolean checkSources,
		        List<Source> sources,

		        String location,

		        boolean checkStatuses,
		        List<LeadStatus> statuses,

		        boolean checkLeadQualities,
		        List<LeadQuality> leadQualities,

		        boolean checkLetsWorkCentres,
		        List<String> letsWorkCentres,

		        String city,
		        String state,
		        String solution,
		        String search,
		        Date fromDate,
		        Date toDate,
		        Pageable pageable
		);

}
