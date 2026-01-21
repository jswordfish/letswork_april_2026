package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Category;
import com.letswork.crm.enums.CategoryType;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByNameAndCompanyIdAndCategoryType(String name, String companyId, CategoryType categoryType);

    List<Category> findByCompanyIdAndCategoryType(String companyId, CategoryType categoryType);
    
}
