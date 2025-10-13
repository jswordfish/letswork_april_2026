package com.letswork.crm.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.TenantRepo;
import com.letswork.crm.service.TenantService;




@Service
@Transactional
public class TenantServiceImpl implements TenantService{
	
	@Autowired
	TenantRepo tenantRepo;
	
	ModelMapper mapper = new ModelMapper();

	@Override
	public Tenant findByIdentity(String orgName, String companyId, String tenantAdminEmail) {
		// TODO Auto-generated method stub
		return tenantRepo.findByIdentity(orgName, companyId, tenantAdminEmail);
	}

	@Override
	public Tenant saveOrUpdate(Tenant tenant) {
		// TODO Auto-generated method stub
		Tenant tenant2 = findByIdentity(tenant.getOrgName(), tenant.getCompanyId(), tenant.getTenantAdminEmail());
		if(tenant2 == null) {
			if(tenantRepo.findTenantByCompanyId(tenant.getCompanyId()) != null) {
				//means tenant is already created with a different org name or admin email. this should not be allowed.
				throw new RuntimeException("Tenant already exists with comp name "+tenant.getOrgName()+" and admin email "+tenant.getTenantAdminEmail());
			}
			
			tenant.setCreateDate(new Date());
			return tenantRepo.save(tenant);
		}
		else {
			tenant.setCreateDate(tenant2.getCreateDate());
			tenant.setUpdateDate(new Date());
			tenant.setId(tenant2.getId());
			mapper.map(tenant, tenant2);
			return tenantRepo.save(tenant2);
		}
	}

	@Override
	public Boolean isCompanyIdExisting(String companyId) {
		// TODO Auto-generated method stub
		 Tenant ten =  tenantRepo.findTenantByCompanyId(companyId);
		 	if(ten != null) {
		 		return true;
		 	}
		 	else {
		 		return false;
		 	}
	}

	@Override
	public List<Tenant> findAll() {
		// TODO Auto-generated method stub
		Iterable<Tenant> itr =  tenantRepo.findAll();
		List<Tenant> list = new ArrayList<>();
		itr.forEach(t -> list.add(t));
		return list;
	}

	@Override
	public Tenant findTenantByCompanyId(String companyId) {
		// TODO Auto-generated method stub
		return tenantRepo.findTenantByCompanyId(companyId);
	}
	
	

}
