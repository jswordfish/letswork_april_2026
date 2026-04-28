package com.letswork.crm.serviceImpl;

import java.time.LocalDate;
import java.util.Date;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ConferenceBundle;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.SortingOrder;
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CompanyId invalid - " + bundle.getCompanyId());
        }
        
        if(bundle.getNumberOfHours() == null) {
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No of Hours can not be null");
        }
        
        if(bundle.getValidForDays() == null) {
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid for Days can not be null");
        }
        
        if(bundle.getPrice() == null) {
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price can not be null");
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
    public PaginatedResponseDto getConferenceBundles(
            String companyId,
            Boolean showInApp,
            LocalDate fromDate,
            LocalDate toDate,
            String sortBy,
            SortingOrder order,
            int page,
            int size
    ) {

        Sort sort = order.equals(SortingOrder.DESC)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ConferenceBundle> result =
                repo.filter(
                        companyId,
                        showInApp,
                        fromDate == null ? null : fromDate.atStartOfDay(),
                        toDate == null ? null : toDate.atTime(23, 59, 59),
                        pageable
                );

        return buildResponse(result, page, size);
    }
    
    private PaginatedResponseDto buildResponse(Page<?> resultPage, int page, int size) {

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
