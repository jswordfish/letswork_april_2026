package com.letswork.crm.serviceImpl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.letswork.crm.entities.Offers;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.OffersRepository;
import com.letswork.crm.service.OffersService;
import com.letswork.crm.service.TenantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OffersServiceImpl implements OffersService {

    private final OffersRepository offersRepository;
    private final TenantService tenantService;

    private final ModelMapper mapper = new ModelMapper();

    @Override
    public Offers saveOrUpdate(Offers offer) {

        Tenant tenant =
                tenantService.findTenantByCompanyId(offer.getCompanyId());

        if (tenant == null) {
            throw new RuntimeException(
                    "Invalid companyId: " + offer.getCompanyId()
            );
        }

        Optional<Offers> existingOpt =
                offersRepository.findByNameAndCompanyId(
                        offer.getName(),
                        offer.getCompanyId()
                );

        if (existingOpt.isPresent()) {

            Offers existing = existingOpt.get();

            offer.setId(existing.getId());
            offer.setCreateDate(existing.getCreateDate());
            offer.setUpdateDate(new Date());

            mapper.map(offer, existing);
            return offersRepository.save(existing);

        } else {
        	offer.setActive(true);
            offer.setCreateDate(new Date());
            offer.setUpdateDate(new Date());
            return offersRepository.save(offer);
        }
    }

    @Override
    public List<Offers> getAllByCompanyId(String companyId) {
        return offersRepository.findByCompanyId(companyId);
    }

    @Override
    public Offers getByCodeAndCompanyId(String code, String companyId) {

        return offersRepository
                .findByCodeAndCompanyId(code, companyId)
                .orElseThrow(() ->
                        new RuntimeException("Offer not found")
                );
    }

	@Override
	public Offers disAbleOffer(Offers offer) {
		
		offer.setActive(false);
		offersRepository.save(offer);
		return offer;
        
	}
}