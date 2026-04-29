package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.SolutionType;

@Repository
public interface SolutionTypeRepository extends JpaRepository<SolutionType, Long> {

    SolutionType findByNameAndCompanyId(String name, String companyId);

    List<SolutionType> findAllByCompanyId(String companyId);
    
}
