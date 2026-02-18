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
import com.letswork.crm.entities.DayPassLimit;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.DayPassLimitRepo;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.service.DayPassLimitService;
import com.letswork.crm.service.TenantService;

@Service
public class DayPassLimitServiceImpl implements DayPassLimitService {

    @Autowired
    private DayPassLimitRepo repo;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private LetsWorkCentreRepository letsWorkCentreRepo;

    private final ModelMapper mapper = new ModelMapper();

    // ✅ SAVE OR UPDATE
    @Override
    public DayPassLimit saveOrUpdate(DayPassLimit dayPassLimit) {

        Tenant tenant = tenantService.findTenantByCompanyId(dayPassLimit.getCompanyId());

        if (tenant == null) {
            throw new RuntimeException("CompanyId invalid - " + dayPassLimit.getCompanyId());
        }

        LetsWorkCentre centre = letsWorkCentreRepo
                .findByNameAndCompanyIdAndCityAndState(
                        dayPassLimit.getLetsWorkCentre(),
                        dayPassLimit.getCompanyId(),
                        dayPassLimit.getCity(),
                        dayPassLimit.getState()
                );

        if (centre == null) {
            throw new RuntimeException("This LetsWorkCentre does not exist");
        }

        DayPassLimit existing = repo
                .findByLetsWorkCentreAndCompanyIdAndCityAndState(
                        dayPassLimit.getLetsWorkCentre(),
                        dayPassLimit.getCompanyId(),
                        dayPassLimit.getCity(),
                        dayPassLimit.getState()
                );

        if (existing != null) {

            dayPassLimit.setId(existing.getId());
            dayPassLimit.setCreateDate(existing.getCreateDate());
            dayPassLimit.setUpdateDate(new Date());

            mapper.map(dayPassLimit, existing);

            return repo.save(existing);
        }

        dayPassLimit.setCreateDate(new Date());
        return repo.save(dayPassLimit);
    }

    // ✅ LIST WITH FILTER + PAGINATION
    @Override
    public PaginatedResponseDto listDayPassLimits(
            String companyId,
            String letsWorkCentre,
            String city,
            String state,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());

        Page<DayPassLimit> pageResult;

        if (letsWorkCentre != null && city != null && state != null) {

            pageResult = repo.findAllByCompanyIdAndLetsWorkCentreAndCityAndState(
                    companyId, letsWorkCentre, city, state, pageable
            );

        } else {

            pageResult = repo.findAllByCompanyId(companyId, pageable);
        }

        PaginatedResponseDto dto = new PaginatedResponseDto();

        dto.setRecordsFrom((page - 1) * size + 1);
        dto.setRecordsTo(Math.min(page * size, (int) pageResult.getTotalElements()));
        dto.setTotalNumberOfRecords((int) pageResult.getTotalElements());
        dto.setTotalNumberOfPages(pageResult.getTotalPages());
        dto.setSelectedPage(page);
        dto.setList(pageResult.getContent());

        return dto;
    }
}
