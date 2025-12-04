package com.letswork.crm.serviceImpl;

import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.entities.Roles;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.RolesRepository;
import com.letswork.crm.service.RolesService;
import com.letswork.crm.service.TenantService;

@Service
public class RolesServiceImpl implements RolesService {

    @Autowired
    private RolesRepository repo;

    @Autowired
    private TenantService tenantService;

    ModelMapper mapper = new ModelMapper();

    @Override
    public Roles saveOrUpdate(Roles role) {

        // Validate company
        Tenant tenant = tenantService.findTenantByCompanyId(role.getCompanyId());
        if (tenant == null) {
            throw new RuntimeException("Invalid companyId - " + role.getCompanyId());
        }

        // Check if role already exists
        Roles existing = repo.findByNameAndCompanyId(role.getName(), role.getCompanyId());

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
    public List<Roles> listByCompanyId(String companyId) {
        return repo.findByCompanyId(companyId);
    }
}
