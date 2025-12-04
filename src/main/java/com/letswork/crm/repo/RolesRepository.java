package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Rbac_entity;

@Repository
public interface RolesRepository extends JpaRepository<Rbac_entity, Long> {
	
	Rbac_entity findByNameAndCompanyId(String name, String companyId);

    List<Rbac_entity> findByCompanyId(String companyId);

}
