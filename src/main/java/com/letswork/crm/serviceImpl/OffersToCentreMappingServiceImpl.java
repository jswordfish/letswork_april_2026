package com.letswork.crm.serviceImpl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.OfferLetsworkCentreMappingDto;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.OffersToCentreMapping;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.OffersToCentreMappingRepository;
import com.letswork.crm.service.OffersToCentreMappingService;

@Service
@Transactional
public class OffersToCentreMappingServiceImpl
        implements OffersToCentreMappingService {

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

        mappingRepo.deleteByOfferName(dto.getOfferName());

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
    public List<OffersToCentreMapping> getByOfferName(String offerName) {
        return mappingRepo.findByOfferName(offerName);
    }
}
