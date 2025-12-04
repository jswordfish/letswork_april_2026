package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Roles;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {
	
	Roles findByNameAndCompanyId(String name, String companyId);

    List<Roles> findByCompanyId(String companyId);

}
