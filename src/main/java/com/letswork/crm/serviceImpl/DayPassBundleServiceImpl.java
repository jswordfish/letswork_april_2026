package com.letswork.crm.serviceImpl;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.letswork.crm.entities.DayPassBundle;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.DayPassBundleRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.service.DayPassBundleService;
import com.letswork.crm.service.TenantService;

@Service
@Transactional
public class DayPassBundleServiceImpl implements DayPassBundleService {

    private final DayPassBundleRepository repo;
    private final TenantService tenantService;
    private final LetsWorkCentreRepository centreRepo;
    private final ModelMapper mapper = new ModelMapper();

    public DayPassBundleServiceImpl(
            DayPassBundleRepository repo,
            TenantService tenantService,
            LetsWorkCentreRepository centreRepo
    ) {
        this.repo = repo;
        this.tenantService = tenantService;
        this.centreRepo = centreRepo;
    }

    @Override
    public DayPassBundle saveOrUpdate(DayPassBundle bundle) {

        Tenant tenant = tenantService.findTenantByCompanyId(bundle.getCompanyId());
        if (tenant == null) {
            throw new RuntimeException("CompanyId invalid - " + bundle.getCompanyId());
        }

        LetsWorkCentre centre =
                centreRepo.findByNameAndCompanyIdAndCityAndState(
                        bundle.getLetsWorkCentre().getName(),
                        bundle.getCompanyId(),
                        bundle.getLetsWorkCentre().getCity(),
                        bundle.getLetsWorkCentre().getState()
                );

        if (centre == null) {
            throw new RuntimeException("This LetsWorkCentre does not exist");
        }
        
        if(bundle.getPrice() == null || bundle.getValidForDays() == null || bundle.getNumberOfDays() == null) {
        	throw new RuntimeException("Price or Number Of Days or Valid for Days is null");
        }
        
        if(bundle.getValidForDays() < bundle.getNumberOfDays()) {
        	throw new RuntimeException("'Valid for days' can not be lesser than 'Number of Days'");
        }
        

        DayPassBundle existing =
                repo.findByLetsWorkCentreAndCompanyIdAndCityAndStateAndNumberOfDays(
                        bundle.getLetsWorkCentre().getName(),
                        bundle.getCompanyId(),
                        bundle.getLetsWorkCentre().getCity(),
                        bundle.getLetsWorkCentre().getState(),
                        bundle.getNumberOfDays()
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
    public List<DayPassBundle> getAllByCompanyId(String companyId) {
        return repo.findAllByCompanyId(companyId);
    }

	@Override
	public List<DayPassBundle> getByCentres(String letsWorkCentre, String companyId, String city, String state) {
		// TODO Auto-generated method stub
		return repo.findByLetsWorkCentreAndCompanyIdAndCityAndState(letsWorkCentre, companyId, city, state);
	}

//	@Override
//	public DayPassBundle saveOrUpdate(DayPassBundle bundle) {
//		// TODO Auto-generated method stub
//		return null;
//	}
}
