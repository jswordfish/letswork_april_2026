package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.SubCategory;
import com.letswork.crm.enums.CategoryType;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    SubCategory findByNameAndCompanyIdAndCategoryType(String name, String companyId, CategoryType categoryType);

    List<SubCategory> findByCompanyIdAndCategoryType(String companyId, CategoryType categoryType);

    List<SubCategory> findByCompanyIdAndParentCategoryAndCategoryType(
            String companyId,
            String parentCategory,
            CategoryType categoryType
    );
    
    void deleteAllByIdIn(List<Long> ids);
    
}
