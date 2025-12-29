package com.letswork.crm.serviceImpl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.letswork.crm.entities.ConferenceBundle;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.ConferenceBundleRepository;
import com.letswork.crm.service.ConferenceBundleService;
import com.letswork.crm.service.TenantService;

@Service
@Transactional
public class ConferenceBundleServiceImpl implements ConferenceBundleService {

    private final ConferenceBundleRepository repo;
    private final TenantService tenantService;
    private final ModelMapper mapper = new ModelMapper();

    public ConferenceBundleServiceImpl(
            ConferenceBundleRepository repo,
            TenantService tenantService
    ) {
        this.repo = repo;
        this.tenantService = tenantService;
    }

    @Override
    public ConferenceBundle saveOrUpdate(ConferenceBundle bundle) {

        Tenant tenant = tenantService.findTenantByCompanyId(bundle.getCompanyId());
        if (tenant == null) {
            throw new RuntimeException("CompanyId invalid - " + bundle.getCompanyId());
        }

        ConferenceBundle existing =
                repo.findByNumberOfHoursAndCompanyId(
                        bundle.getNumberOfHours(),
                        bundle.getCompanyId()
                );

        if (existing != null) {
            bundle.setId(existing.getId());
            bundle.setCreateDate(existing.getCreateDate());
            bundle.setUpdateDate(new Date());
            mapper.map(bundle, existing);
            return repo.save(existing);
        } else {
            bundle.setCreateDate(new Date());
            bundle.setUpdateDate(new Date());
            return repo.save(bundle);
        }
    }

    @Override
    public List<ConferenceBundle> getAllByCompanyId(String companyId) {
        return repo.findAllByCompanyId(companyId);
    }
}
