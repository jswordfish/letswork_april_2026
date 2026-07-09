package com.letswork.crm.serviceImpl;

import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.entities.SolutionType;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.SolutionTypeRepository;
import com.letswork.crm.service.SolutionTypeService;
import com.letswork.crm.service.TenantService;

@Service
public class SolutionTypeServiceImpl implements SolutionTypeService {

    @Autowired
    private SolutionTypeRepository repo;

    @Autowired
    private TenantService tenantService;

    ModelMapper mapper = new ModelMapper();

    @Override
    public SolutionType saveOrUpdate(SolutionType solutionType) {

        Tenant tenant = tenantService.findTenantByCompanyId(solutionType.getCompanyId());

        if (tenant == null) {
            throw new RuntimeException("CompanyId invalid - " + solutionType.getCompanyId());
        }

        SolutionType existing = repo.findByNameAndUnitAndCompanyId(
                solutionType.getName(),
                solutionType.getUnit(),
                solutionType.getCompanyId()
        );

        if (existing != null) {
            solutionType.setId(existing.getId());
            solutionType.setCreateDate(existing.getCreateDate());
            solutionType.setUpdateDate(new Date());

            mapper.map(solutionType, existing);
            return repo.save(existing);

        } else {
            solutionType.setCreateDate(new Date());
            solutionType.setUpdateDate(new Date());
            return repo.save(solutionType);
        }
    }

    @Override
    public List<SolutionType> getSolutionTypes(
            String companyId,
            String search
    ) {

        if (search != null && search.trim().isEmpty()) {
            search = null;
        }

        return repo.search(companyId, search);
    }
}
