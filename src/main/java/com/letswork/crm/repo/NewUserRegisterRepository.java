package com.letswork.crm.repo;

import java.util.List;
import java.util.Optional;

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

    
}