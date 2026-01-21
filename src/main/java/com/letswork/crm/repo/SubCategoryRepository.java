package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.SubCategory;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    SubCategory findByNameAndCompanyId(String name, String companyId);

    List<SubCategory> findByCompanyId(String companyId);

    List<SubCategory> findByCompanyIdAndParentCategory(
            String companyId,
            String parentCategory
    );
    
    void deleteAllByIdIn(List<Long> ids);
    
}
