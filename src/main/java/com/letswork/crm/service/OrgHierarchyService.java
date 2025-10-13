package com.letswork.crm.service;

import java.util.List;

import com.letswork.crm.entities.OrgHierarchy;





public interface OrgHierarchyService {
	
	 public List<OrgHierarchy> findByLevel(String level, String companyId);
	 
	 public OrgHierarchy findByRoleOrDesig(String roleOrDesig, String companyId);
	 
	 public OrgHierarchy findByParentRoleOrDesig(String roleOrDesig, String companyId);
	 
	 public OrgHierarchy saveOrUpdate(OrgHierarchy orgHierarchy);
	 
	 public List<OrgHierarchy> getAllHierarchyLevels(String companyId);

}
