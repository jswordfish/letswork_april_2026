package com.letswork.crm.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

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
@Transactional
public class RolesServiceImpl implements RolesService {

    @Autowired
    private RolesRepository repo;
    
    @Autowired
    OrgHierarchyRepo orgRepo;

    @Autowired
    private TenantService tenantService;

    ModelMapper mapper = new ModelMapper();

    @Override
    public RbacRoleResponseDTO saveOrUpdateGrouped(RbacRoleResponseDTO dto, String companyId) {

        // 1️⃣ Validate company
        Tenant tenant = tenantService.findTenantByCompanyId(companyId);
        if (tenant == null) {
            throw new RuntimeException("Invalid companyId - " + companyId);
        }

        // 2️⃣ Validate role exists in OrgHierarchy
        OrgHierarchy org = orgRepo.findByRoleOrDesig(dto.getName(), companyId);
        if (org == null) {
            throw new RuntimeException("Role does not exist - " + dto.getName());
        }

        // 3️⃣ Delete existing permissions for role (FULL REPLACE)
        repo.deleteByNameAndCompanyId(dto.getName(), companyId);

        // 4️⃣ Insert new permissions
        for (Map.Entry<String, MenuPermissionDTO> entry : dto.getMenu_items().entrySet()) {

            String menuItem = entry.getKey();
            MenuPermissionDTO perm = entry.getValue();

            Rbac_entity entity = new Rbac_entity();
            entity.setName(dto.getName());
            entity.setCompanyId(companyId);
            entity.setMenuItem(menuItem);

            entity.setPage_create(Boolean.TRUE.equals(perm.getPage_create()));
            entity.setPage_edit(Boolean.TRUE.equals(perm.getPage_edit()));
            entity.setPage_delete(Boolean.TRUE.equals(perm.getPage_delete()));
            entity.setPage_view(Boolean.TRUE.equals(perm.getPage_view()));

            entity.setCreateDate(new Date());
            entity.setUpdateDate(new Date());

            repo.save(entity);
        }

        // 5️⃣ Return grouped response
        return getRoleGrouped(dto.getName(), companyId);
    }

    @Override
    public List<RbacRoleResponseDTO> listGroupedByCompany(String companyId) {

        List<Rbac_entity> list = repo.findByCompanyId(companyId);

        // roleName -> DTO
        Map<String, RbacRoleResponseDTO> roleMap = new LinkedHashMap<>();

        for (Rbac_entity e : list) {

            if (e.getName() == null || e.getMenuItem() == null) {
                continue;
            }

            String role = e.getName().trim();
            String menuItem = e.getMenuItem();

            // Create role DTO if not exists
            RbacRoleResponseDTO roleDto =
                    roleMap.computeIfAbsent(role, r -> {
                        RbacRoleResponseDTO dto = new RbacRoleResponseDTO();
                        dto.setName(r);
                        dto.setMenu_items(new LinkedHashMap<>());
                        return dto;
                    });

            MenuPermissionDTO perm = new MenuPermissionDTO();
            perm.setPage_create(Boolean.TRUE.equals(e.getPage_create()));
            perm.setPage_edit(Boolean.TRUE.equals(e.getPage_edit()));
            perm.setPage_delete(Boolean.TRUE.equals(e.getPage_delete()));
            perm.setPage_view(Boolean.TRUE.equals(e.getPage_view()));

            roleDto.getMenu_items().put(menuItem, perm);
        }

        return new ArrayList<>(roleMap.values());
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

	    // LinkedHashMap to preserve insertion order
	    Map<String, MenuPermissionDTO> menuItems = new LinkedHashMap<>();

	    for (Rbac_entity e : list) {

	        if (e.getMenuItem() == null || e.getMenuItem().trim().isEmpty()) {
	            continue; // skip invalid menu items
	        }

	        String normalizedMenuItem = e.getMenuItem();

	        MenuPermissionDTO perm = new MenuPermissionDTO();
	        perm.setPage_create(Boolean.TRUE.equals(e.getPage_create()));
	        perm.setPage_edit(Boolean.TRUE.equals(e.getPage_edit()));
	        perm.setPage_delete(Boolean.TRUE.equals(e.getPage_delete()));
	        perm.setPage_view(Boolean.TRUE.equals(e.getPage_view()));

	        menuItems.put(normalizedMenuItem, perm);
	    }

	    dto.setMenu_items(menuItems);
	    return dto;
	}
	
	
	
}
