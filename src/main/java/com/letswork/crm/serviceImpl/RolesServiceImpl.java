package com.letswork.crm.serviceImpl;

import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.entities.OrgHierarchy;
import com.letswork.crm.entities.Rbac_entity;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.OrgHierarchyRepo;
import com.letswork.crm.repo.RolesRepository;
import com.letswork.crm.service.RolesService;
import com.letswork.crm.service.TenantService;

@Service
public class RolesServiceImpl implements RolesService {

    @Autowired
    private RolesRepository repo;
    
    @Autowired
    OrgHierarchyRepo orgRepo;

    @Autowired
    private TenantService tenantService;

    ModelMapper mapper = new ModelMapper();

    @Override
    public Rbac_entity saveOrUpdate(Rbac_entity role) {

        // Validate company
        Tenant tenant = tenantService.findTenantByCompanyId(role.getCompanyId());
        if (tenant == null) {
            throw new RuntimeException("Invalid companyId - " + role.getCompanyId());
        }
        
        OrgHierarchy org = orgRepo.findByRoleOrDesig(role.getName(), role.getCompanyId());
        if(org==null) {
        	throw new RuntimeException("This role does not exists");
        }

        // Check if role already exists
        Rbac_entity existing = repo.findByNameAndCompanyId(role.getName(), role.getCompanyId());

        if (existing != null) {
            // Update existing
            role.setId(existing.getId());
            role.setCreateDate(existing.getCreateDate());
            role.setUpdateDate(new Date());

            mapper.map(role, existing);
            return repo.save(existing);

        } else {
            // Save new role
            role.setCreateDate(new Date());
            role.setUpdateDate(new Date());
            return repo.save(role);
        }
    }

    @Override
    public List<Rbac_entity> listByCompanyId(String companyId) {
        return repo.findByCompanyId(companyId);
    }
}
