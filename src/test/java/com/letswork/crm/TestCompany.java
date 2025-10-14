package com.letswork.crm;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import com.letswork.crm.entities.Tenant;
import com.letswork.crm.service.TenantService;
@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class TestCompany {
	
	
	@Autowired
	TenantService tenantService;
	
	@Test
	@Rollback(value = false)
	public void testSaveUpdateCompany() {
		Tenant tenant = Tenant.builder().companyId("LW")
				.companyName("Letswork")
				.orgName("Letswork")
				.build();
		tenantService.saveOrUpdate(tenant);
	}

}
