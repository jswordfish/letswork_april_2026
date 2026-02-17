package com.letswork.crm.serviceImpl;


import java.util.Date;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.SeatConfig;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.SeatType;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.SeatConfigRepository;
import com.letswork.crm.service.SeatConfigService;
import com.letswork.crm.service.TenantService;

@Service
public class SeatConfigServiceImpl implements SeatConfigService {

    @Autowired
    private SeatConfigRepository repo;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private LetsWorkCentreRepository letsWorkCentreRepo;

    @Autowired
    private ModelMapper mapper;

    @Override
    public SeatConfig saveOrUpdate(SeatConfig seatConfig) {

        Tenant tenant = tenantService.findTenantByCompanyId(seatConfig.getCompanyId());
        if (tenant == null) {
            throw new RuntimeException("CompanyId invalid - " + seatConfig.getCompanyId());
        }

        LetsWorkCentre centre =
                letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(
                        seatConfig.getLetsWorkCentre(),
                        seatConfig.getCompanyId(),
                        seatConfig.getCity(),
                        seatConfig.getState()
                );

        if (centre == null) {
            throw new RuntimeException("This LetsWorkCentre does not exist");
        }

        // 🔹 Update flow
        if (seatConfig.getId() != null) {

            SeatConfig existing =
                    repo.findByIdAndCompanyId(
                            seatConfig.getId(),
                            seatConfig.getCompanyId()
                    ).orElseThrow(() ->
                            new RuntimeException("SeatConfig not found")
                    );

            seatConfig.setCreateDate(existing.getCreateDate());
            seatConfig.setUpdateDate(new Date());

            mapper.map(seatConfig, existing);
            return repo.save(existing);
        }

        // 🔹 Create flow
        seatConfig.setCreateDate(new Date());
        seatConfig.setUpdateDate(new Date());

        return repo.save(seatConfig);
    }

    @Override
    public PaginatedResponseDto listSeatConfigs(
            String companyId,
            String letsWorkCentre,
            String city,
            String state,
            SeatType seatType,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

        Page<SeatConfig> seatConfigPage =
                repo.searchSeatConfigs(companyId, letsWorkCentre, city, state, seatType, pageable);

        PaginatedResponseDto dto = new PaginatedResponseDto();
        dto.setRecordsFrom((page - 1) * size + 1);
        dto.setRecordsTo(Math.min(page * size, (int) seatConfigPage.getTotalElements()));
        dto.setTotalNumberOfRecords((int) seatConfigPage.getTotalElements());
        dto.setTotalNumberOfPages(seatConfigPage.getTotalPages());
        dto.setSelectedPage(page);
        dto.setList(seatConfigPage.getContent());

        return dto;
    }
}
