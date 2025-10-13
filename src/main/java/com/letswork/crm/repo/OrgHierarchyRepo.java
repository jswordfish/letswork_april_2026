package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.OrgHierarchy;




@Repository
public interface OrgHierarchyRepo extends CrudRepository<OrgHierarchy, Long> {

	
	 public List<OrgHierarchy> findByLevelAndCompanyId(String level, String companyId);
	 
	 
	 @Query("select o from OrgHierarchy o where o.roleOrDesig =:roleOrDesig and o.companyId =:companyId")
	 public OrgHierarchy findByRoleOrDesig(@Param("roleOrDesig")  String roleOrDesig, String companyId);
	 
	 @Query("select o from OrgHierarchy o where o.parentRoleOrDesig =:parentRoleOrDesig and o.companyId =:companyId")
	 public OrgHierarchy findByParentRoleOrDesig(@Param("parentRoleOrDesig")  String parentRoleOrDesig, String companyId);
	 
	 
	 public List<OrgHierarchy> findByCompanyId( String companyId);
	 
}

