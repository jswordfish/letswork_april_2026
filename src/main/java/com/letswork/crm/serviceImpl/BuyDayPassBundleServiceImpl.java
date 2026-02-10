package com.letswork.crm.serviceImpl;

import java.time.LocalDateTime;
import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.BuyDayPassRequestDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.BuyDayPassBundle;
import com.letswork.crm.entities.DayPassBundle;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.BuyDayPassBundleRepository;
import com.letswork.crm.repo.DayPassBundleRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.repo.NewUserRegisterRepository;
import com.letswork.crm.service.BuyDayPassBundleService;
import com.letswork.crm.service.LetsWorkClientService;
import com.letswork.crm.service.NewUserRegisterService;
import com.letswork.crm.service.TenantService;

@Service
@Transactional
public class BuyDayPassBundleServiceImpl
        implements BuyDayPassBundleService {

    private final BuyDayPassBundleRepository buyRepo;
    private final DayPassBundleRepository bundleRepo;
    private final TenantService tenantService;
    private final NewUserRegisterRepository newUserRegisterRepository;
    private final NewUserRegisterService newUserRegisterService;
    private final LetsWorkClientRepository letsWorkClientRepository;
    private final LetsWorkClientService letsWorkClientService;

    public BuyDayPassBundleServiceImpl(
            BuyDayPassBundleRepository buyRepo,
            DayPassBundleRepository bundleRepo,
            TenantService tenantService,
            NewUserRegisterRepository newUserRegisterRepository,
            NewUserRegisterService newUserRegisterService,
            LetsWorkClientRepository letsWorkClientRepository,
            LetsWorkClientService letsWorkClientService
    ) {
        this.buyRepo = buyRepo;
        this.bundleRepo = bundleRepo;
        this.tenantService = tenantService;
        this.newUserRegisterRepository = newUserRegisterRepository;
        this.newUserRegisterService = newUserRegisterService;
        this.letsWorkClientRepository = letsWorkClientRepository;
        this.letsWorkClientService = letsWorkClientService;
    }

    @Override
    public BuyDayPassBundle purchase(BuyDayPassRequestDto dto) {

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new RuntimeException("Email is required");
        }

        if (dto.getBundleId() == null) {
            throw new RuntimeException("BundleId is required");
        }

        Tenant tenant =
                tenantService.findTenantByCompanyId(dto.getCompanyId());

        if (tenant == null) {
            throw new RuntimeException(
                    "Invalid companyId: " + dto.getCompanyId()
            );
        }

        DayPassBundle bundle =
                bundleRepo.findById(dto.getBundleId())
                        .orElseThrow(() ->
                                new RuntimeException("DayPass bundle not found")
                        );
        
        LetsWorkClient client =
        		letsWorkClientRepository.findByEmailAndCompanyId(dto.getEmail(), dto.getCompanyId())
                .orElseThrow(() ->
                        new RuntimeException("Company not found")
                );
        
        

        BuyDayPassBundle buy = new BuyDayPassBundle();

        buy.setCompanyId(dto.getCompanyId());
        buy.setEmail(dto.getEmail());
        buy.setBundleId(bundle.getId());
        buy.setNumberOfDays(bundle.getNumberOfDays());
        buy.setLetsWorkCentre(bundle.getLetsWorkCentre());
        buy.setCity(bundle.getCity());
        buy.setState(bundle.getState());
        LocalDateTime now = LocalDateTime.now();
        buy.setPurchaseDate(now);

        buy.setExpiryDate(
                now.plusDays(
                        Long.parseLong(bundle.getValidForDays())
                )
        );

        buy.setCreateDate(new Date());
        buy.setUpdateDate(new Date());
        
        letsWorkClientService.updateDayPass(buy.getNumberOfDays(), buy.getEmail(), buy.getCompanyId());

        return buyRepo.save(buy);
     
    }

    @Override
    public PaginatedResponseDto getPaginated(
            String companyId,
            String email,
            Long bundleId,
            String letsWorkCentre,
            String city,
            String state,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            int page,
            int size
    ) {

        
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("purchaseDate").descending()
        );

        Page<BuyDayPassBundle> resultPage = buyRepo.findByFilters(
                companyId,
                email,
                bundleId,
                letsWorkCentre,
                city,
                state,
                fromDate,
                toDate,
                pageable
        );

        PaginatedResponseDto dto = new PaginatedResponseDto();
        dto.setSelectedPage(page);
        dto.setTotalNumberOfRecords((int) resultPage.getTotalElements());
        dto.setTotalNumberOfPages(resultPage.getTotalPages());
        dto.setRecordsFrom(page * size + 1);
        dto.setRecordsTo(
                Math.min((page + 1) * size, (int) resultPage.getTotalElements())
        );
        dto.setList(resultPage.getContent());

        return dto;
    }
}
