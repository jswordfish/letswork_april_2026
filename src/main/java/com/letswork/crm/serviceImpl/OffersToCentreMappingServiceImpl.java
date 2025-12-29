package com.letswork.crm.serviceImpl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.OfferLetsworkCentreMappingDto;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.OffersToCentreMapping;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.OffersToCentreMappingRepository;
import com.letswork.crm.service.OffersToCentreMappingService;
import com.letswork.crm.service.TenantService;

@Service
@Transactional
public class OffersToCentreMappingServiceImpl
        implements OffersToCentreMappingService {
	
	@Autowired
	TenantService tenantService;

    private final OffersToCentreMappingRepository mappingRepo;
    private final LetsWorkCentreRepository centreRepo;

    public OffersToCentreMappingServiceImpl(
            OffersToCentreMappingRepository mappingRepo,
            LetsWorkCentreRepository centreRepo
    ) {
        this.mappingRepo = mappingRepo;
        this.centreRepo = centreRepo;
    }

    @Override
    public String saveOrUpdate(OfferLetsworkCentreMappingDto dto) {

        if (dto.getOfferName() == null || dto.getOfferName().isBlank()) {
            throw new RuntimeException("Offer name is required");
        }

        if (dto.getCenterIds() == null || dto.getCenterIds().isEmpty()) {
            throw new RuntimeException("At least one centre must be selected");
        }
        
		Tenant tenant = tenantService.findTenantByCompanyId(dto.getCompanyId());
		
		if(tenant==null) {
			
			throw new RuntimeException("CompanyId invalid - "+dto.getCompanyId());
			
		}
		
		

        mappingRepo.deleteByOfferNameAndCompanyId(dto.getOfferName(), dto.getCompanyId());

        for (Long centreId : dto.getCenterIds()) {

            LetsWorkCentre centre = centreRepo.findById(centreId)
                    .orElseThrow(() ->
                            new RuntimeException("LetsWorkCentre not found: " + centreId)
                    );

            OffersToCentreMapping mapping = new OffersToCentreMapping();
            mapping.setOfferName(dto.getOfferName());
            mapping.setLetsWorkCentre(centre);

            mappingRepo.save(mapping);
        }

        return "Offer-centre mapping saved successfully";
    }

    @Override
    public List<OffersToCentreMapping> getByOfferName(String offerName, String companyId) {
        return mappingRepo.findByOfferNameAndCompanyId(offerName, companyId);
    }
}
