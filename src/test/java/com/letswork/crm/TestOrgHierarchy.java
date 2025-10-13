package com.letswork.crm;

import java.io.File;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.letswork.crm.entities.OrgHierarchy;
import com.letswork.crm.service.OrgHierarchyService;
import com.poiji.bind.Poiji;
@SpringBootTest
@Transactional
public class TestOrgHierarchy {
	
	@Autowired
	OrgHierarchyService orgHierarchyService;
	@Test
	public void testGetOrg() {
		 List<OrgHierarchy> list =  orgHierarchyService.getAllHierarchyLevels("LW");
		 for(OrgHierarchy o : list) {
			 System.out.println(o.getRoleOrDesig()+" - "+o.getParentRoleOrDesig()+" - "+o.getCompanyId());
		 }
	}

	
	@Test
	public void testUploadOrgHierarchy() {
		File file = new File("/Users/jatinsutaria/Downloads/org_hierarchy_letswork.xlsx");
		List<OrgHierarchy> levels = Poiji.fromExcel(file, OrgHierarchy.class);
		System.out.println("Printing List Data: " +levels);
		for(OrgHierarchy level : levels) {
			String parent = level.getParentRoleOrDesig();
				if(parent != null) {
					OrgHierarchy p =  orgHierarchyService.findByRoleOrDesig(parent, "LW");
					if(p == null) {
						throw new RuntimeException("parent level should exist "+level.getRoleOrDesig());
					}
				}
				orgHierarchyService.saveOrUpdate(level);
			
		}
	}
}
