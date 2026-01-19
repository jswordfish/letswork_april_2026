package com.letswork.crm.repo;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Referral;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {

    Optional<Referral> findByEmailAndCompanyId(String email, String companyId);

    Optional<Referral> findByEmailAndEmailOfUserAndCompanyId(
            String email,
            String emailOfUser,
            String companyId
    );

    @Query("SELECT r FROM Referral r " +
    	       "WHERE r.companyId = :companyId " +
    	       "AND (:email IS NULL OR r.email = :email) " +
    	       "AND (:name IS NULL OR r.name LIKE %:name%) " +
    	       "AND (:emailOfUser IS NULL OR r.emailOfUser = :emailOfUser) " +
    	       "AND (:fromDate IS NULL OR r.joiningDate >= :fromDate) " +
    	       "AND (:toDate IS NULL OR r.joiningDate <= :toDate)")
    	Page<Referral> filter(
    	        @Param("companyId") String companyId,
    	        @Param("email") String email,
    	        @Param("name") String name,
    	        @Param("emailOfUser") String emailOfUser,
    	        @Param("fromDate") LocalDate fromDate,
    	        @Param("toDate") LocalDate toDate,
    	        Pageable pageable
    	);
}
