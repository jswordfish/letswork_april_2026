package com.letswork.crm.serviceImpl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.MenuPermissionDTO;
import com.letswork.crm.dtos.RbacRoleResponseDTO;
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
        Rbac_entity existing = repo.findByNameAndCompanyIdAndMenuItem(role.getName(), role.getCompanyId(), role.getMenuItem());

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

	@Override
	public List<Rbac_entity> listByRole(String role, String companyId) {
		// TODO Auto-generated method stub
		return repo.findByNameAndCompanyId(role, companyId);
	}
	
	@Override
    public RbacRoleResponseDTO getRoleGrouped(String role, String companyId) {

        List<Rbac_entity> list = repo.findByNameAndCompanyId(role, companyId);

        RbacRoleResponseDTO dto = new RbacRoleResponseDTO();
        dto.setName(role);

        Map<String, MenuPermissionDTO> menuItems = new HashMap<>();

        for (Rbac_entity e : list) {

            if (e.getMenuItem() == null || e.getMenuItem().trim().isEmpty()) {
                continue; // skip invalid/null menu items
            }

            MenuPermissionDTO perm = new MenuPermissionDTO();
            perm.setPage_create(e.getPage_create());
            perm.setPage_edit(e.getPage_edit());
            perm.setPage_delete(e.getPage_delete());
            perm.setPage_view(e.getPage_view());

            menuItems.put(e.getMenuItem(), perm);
        }

        dto.setMenu_items(menuItems);
        return dto;
    }
	
}
