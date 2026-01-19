package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByNameAndCompanyId(String name, String companyId);

    List<Category> findByCompanyId(String companyId);
    
}
