package com.letswork.crm.serviceImpl;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.BuyConferenceBundleRequestDto;
import com.letswork.crm.entities.BuyConferenceBundle;
import com.letswork.crm.entities.ConferenceBundle;
import com.letswork.crm.repo.BuyConferenceBundleRepository;
import com.letswork.crm.repo.ConferenceBundleRepository;
import com.letswork.crm.service.BuyConferenceBundleService;
import com.letswork.crm.service.NewUserRegisterService;
import com.letswork.crm.service.TenantService;

@Service
@Transactional
public class BuyConferenceBundleServiceImpl
        implements BuyConferenceBundleService {

    private final BuyConferenceBundleRepository buyRepo;
    private final ConferenceBundleRepository bundleRepo;
    private final TenantService tenantService;
    private final NewUserRegisterService newUserRegisterService;

    public BuyConferenceBundleServiceImpl(
            BuyConferenceBundleRepository buyRepo,
            ConferenceBundleRepository bundleRepo,
            TenantService tenantService,
            NewUserRegisterService newUserRegisterService
    ) {
        this.buyRepo = buyRepo;
        this.bundleRepo = bundleRepo;
        this.tenantService = tenantService;
        this.newUserRegisterService = newUserRegisterService;
    }

    @Override
    public BuyConferenceBundle purchase(
            BuyConferenceBundleRequestDto dto
    ) {

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new RuntimeException("Email is required");
        }

        if (dto.getBundleId() == null) {
            throw new RuntimeException("BundleId is required");
        }

        tenantService.findTenantByCompanyId(dto.getCompanyId());

        ConferenceBundle bundle =
                bundleRepo.findById(dto.getBundleId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Conference bundle not found"
                                )
                        );

        BuyConferenceBundle buy = new BuyConferenceBundle();

        buy.setCompanyId(dto.getCompanyId());
        buy.setEmail(dto.getEmail());
        buy.setBundleId(bundle.getId());
        buy.setNumberOfHours(bundle.getNumberOfHours());

        LocalDateTime now = LocalDateTime.now();
        buy.setPurchaseDate(now);

        buy.setExpiryDate(
                now.plusDays(
                        Long.parseLong(bundle.getValidForDays())
                )
        );

        buy.setCreateDate(new Date());
        buy.setUpdateDate(new Date());

        // 🔥 CREDIT UPDATE LOGIC
        newUserRegisterService.updateConferenceCredits(
                buy.getNumberOfHours(),
                buy.getEmail(),
                buy.getCompanyId()
        );

        return buyRepo.save(buy);
    }

    @Override
    public List<BuyConferenceBundle> get(
            String companyId,
            String email,
            Long bundleId
    ) {
        return buyRepo.findByFilters(
                companyId,
                email,
                bundleId
        );
    }
}