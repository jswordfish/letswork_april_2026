package com.letswork.crm.repo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.NewUserRegister;

@Repository
public interface NewUserRegisterRepository extends JpaRepository<NewUserRegister, Long> {

	Optional<NewUserRegister>
	findByEmailAndCompanyId(String email, String companyId);
	
	Optional<NewUserRegister>
	findByIdAndCompanyId(Long id, String companyId);

	Optional<NewUserRegister>
	findByPhoneNumberAndCompanyId(String phoneNumber, String companyId);

    List<NewUserRegister> findByCompanyId(String companyId);
    
    List<NewUserRegister> findByMonthlyTrueAndCompanyId(
            String companyId
    );
    
    @Query(
            "SELECT DISTINCT u.category " +
            "FROM NewUserRegister u " +
            "WHERE u.companyId = :companyId " +
            "AND u.category IS NOT NULL"
        )
        List<String> findDistinctCategories(
                @Param("companyId") String companyId
        );

        @Query(
            "SELECT DISTINCT u.subCategory " +
            "FROM NewUserRegister u " +
            "WHERE u.companyId = :companyId " +
            "AND u.category = :category " +
            "AND u.subCategory IS NOT NULL"
        )
        List<String> findDistinctSubCategories(
                @Param("companyId") String companyId,
                @Param("category") String category
        );

        List<NewUserRegister> findByCompanyIdAndCategoryAndSubCategory(
                String companyId,
                String category,
                String subCategory
        );
        
        @Query(
        	    "SELECT u FROM NewUserRegister u " +
        	    "WHERE u.companyId = :companyId " +
        	    "AND (:email IS NULL OR u.email = :email) " +
        	    "AND (:centre IS NULL OR u.letsWorkCentre = :centre) " +
        	    "AND (:city IS NULL OR u.city = :city) " +
        	    "AND (:state IS NULL OR u.state = :state) " +
        	    "AND (:category IS NULL OR u.category = :category) " +
        	    "AND (:subCategory IS NULL OR u.subCategory = :subCategory) " +
        	    "AND (:fromDate IS NULL OR u.createDate >= :fromDate) " +
        	    "AND (:toDate IS NULL OR u.createDate <= :toDate)"
        	)
        	Page<NewUserRegister> filter(
        	        @Param("companyId") String companyId,
        	        @Param("email") String email,
        	        @Param("centre") String centre,
        	        @Param("city") String city,
        	        @Param("state") String state,
        	        @Param("category") String category,
        	        @Param("subCategory") String subCategory,
        	        @Param("fromDate") Date fromDate,
        	        @Param("toDate") Date toDate,
        	        Pageable pageable
        	);

    
}