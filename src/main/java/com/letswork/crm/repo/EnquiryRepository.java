package com.letswork.crm.repo;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Enquiry;
import com.letswork.crm.enums.EnquiryStatus;
import com.letswork.crm.enums.EnquiryType;

@Repository
public interface EnquiryRepository extends JpaRepository<Enquiry, Long> {

	@Query("SELECT e FROM Enquiry e " +
		       "WHERE e.companyId = :companyId " +
		       "AND (:name IS NULL OR e.name = :name) " +
		       "AND (:email IS NULL OR e.email = :email) " +
		       "AND (:phone IS NULL OR e.phoneNumber = :phone) " +
		       "AND (:letsWorkCentre IS NULL OR e.letsWorkCentre = :letsWorkCentre) " +
		       "AND (:city IS NULL OR e.city = :city) " +
		       "AND (:state IS NULL OR e.state = :state) " +
		       "AND (:fromDate IS NULL OR e.date >= :fromDate) " +
		       "AND (:toDate IS NULL OR e.date <= :toDate) " +
		       "AND (:enquiryType IS NULL OR e.enquiryType = :enquiryType) " +
		       "AND (:enquiryStatus IS NULL OR e.enquiryStatus = :enquiryStatus) " +
		       "AND (:solutionName IS NULL OR LOWER(e.solutions.name) = LOWER(:solutionName)) " +
		       "AND (" +
		       "    :search IS NULL " +
		       "    OR LOWER(e.name) LIKE LOWER(CONCAT('%', CONCAT(:search, '%'))) " +
		       "    OR LOWER(e.email) LIKE LOWER(CONCAT('%', CONCAT(:search, '%'))) " +
		       "    OR LOWER(e.phoneNumber) LIKE LOWER(CONCAT('%', CONCAT(:search, '%'))) " +
		       "    OR LOWER(e.description) LIKE LOWER(CONCAT('%', CONCAT(:search, '%'))) " +
		       "    OR LOWER(e.letsWorkCentre) LIKE LOWER(CONCAT('%', CONCAT(:search, '%'))) " +
		       "    OR LOWER(e.city) LIKE LOWER(CONCAT('%', CONCAT(:search, '%'))) " +
		       "    OR LOWER(e.state) LIKE LOWER(CONCAT('%', CONCAT(:search, '%'))) " +
		       "    OR CAST(e.date AS string) LIKE CONCAT('%', CONCAT(:search, '%')) " +
		       "    OR CAST(e.time AS string) LIKE CONCAT('%', CONCAT(:search, '%')) " +
		       "    OR CAST(e.enquiryType AS string) LIKE CONCAT('%', CONCAT(:search, '%')) " +
		       "    OR CAST(e.enquiryStatus AS string) LIKE CONCAT('%', CONCAT(:search, '%'))" +
		       "    OR LOWER(e.solutions.name) LIKE LOWER(CONCAT('%', CONCAT(:search, '%'))) " +
		       ")")
		Page<Enquiry> findByFilters(
		        @Param("companyId") String companyId,
		        @Param("name") String name,
		        @Param("email") String email,
		        @Param("phone") String phone,
		        @Param("letsWorkCentre") String letsWorkCentre,
		        @Param("city") String city,
		        @Param("state") String state,
		        @Param("search") String search,
		        @Param("fromDate") Date fromDate,
		        @Param("toDate") Date toDate,
		        @Param("enquiryType") EnquiryType enquiryType,
		        @Param("enquiryStatus") EnquiryStatus enquiryStatus,
		        @Param("solutionName") String solutionName,
		        Pageable pageable
		);
	
}
