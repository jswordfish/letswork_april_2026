package com.letswork.crm.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Enquiry;
import com.letswork.crm.enums.EnquiryType;
import com.letswork.crm.enums.Solution;

@Repository
public interface EnquiryRepository extends JpaRepository<Enquiry, Long> {

	@Query(
		    "SELECT e FROM Enquiry e " +
		    "WHERE e.companyId = :companyId " +
		    "AND (:name IS NULL OR e.name = :name) " +
		    "AND (:email IS NULL OR e.email = :email) " +
		    "AND (:phone IS NULL OR e.phoneNumber = :phone) " +
		    "AND (:solution IS NULL OR e.solution = :solution) " +
		    "AND (:fromDate IS NULL OR e.date >= :fromDate) " +
		    "AND (:toDate IS NULL OR e.date <= :toDate) " +
		    "AND (:enquiryType IS NULL OR e.enquiryType = :enquiryType"
		)
		List<Enquiry> findByFilters(
		        @Param("companyId") String companyId,
		        @Param("name") String name,
		        @Param("email") String email,
		        @Param("phone") String phone,
		        @Param("solution") Solution solution,
		        @Param("fromDate") Date fromDate,
		        @Param("toDate") Date toDate,
		        @Param("enquiryType") EnquiryType enquiryType
		);
}
