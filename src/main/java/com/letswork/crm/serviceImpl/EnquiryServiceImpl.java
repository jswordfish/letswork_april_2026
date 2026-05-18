package com.letswork.crm.serviceImpl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.EnquiryDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Enquiry;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.Solutions;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.EnquiryStatus;
import com.letswork.crm.enums.EnquiryType;
import com.letswork.crm.repo.EnquiryRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.SolutionsRepository;
import com.letswork.crm.service.EnquiryService;
import com.letswork.crm.service.TenantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnquiryServiceImpl implements EnquiryService {

    private final EnquiryRepository enquiryRepository;
    
    @Autowired
    TenantService tenantService;
    
    @Autowired
	LetsWorkCentreRepository letsWorkCentreRepo;
    
    @Autowired
    SolutionsRepository solutionsRepo;

    @Override
    public Enquiry createEnquiry(EnquiryDto dto) {

        Tenant tenant = tenantService.findTenantByCompanyId(dto.getCompanyId());

        if (tenant == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CompanyId invalid - " + dto.getCompanyId()
            );
        }

        LetsWorkCentre centre =
                letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(
                        dto.getLetsWorkCentre(),
                        dto.getCompanyId(),
                        dto.getCity(),
                        dto.getState()
                );

        if (centre == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This LetsWorkCentre does not exist"
            );
        }

        Enquiry enquiry = new Enquiry();

        enquiry.setName(dto.getName());
        enquiry.setEmail(dto.getEmail());
        enquiry.setPhoneNumber(dto.getPhoneNumber());
        enquiry.setDescription(dto.getDescription());
        enquiry.setLetsWorkCentre(dto.getLetsWorkCentre());
        enquiry.setCity(dto.getCity());
        enquiry.setState(dto.getState());
        enquiry.setEnquiryType(dto.getEnquiryType());
        enquiry.setEnquiryStatus(EnquiryStatus.RAISED);

        if (dto.getEnquiryType() == EnquiryType.SOLUTIONS) {

            if (dto.getSolutionId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solution id is required for SOLUTIONS enquiry");
            }

            Solutions solution = solutionsRepo.findById(dto.getSolutionId())
                    .orElseThrow(() ->
                            new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solution not found")
                    );

            enquiry.setSolutions(solution);

            // not needed for solution enquiry
            enquiry.setDate(null);
            enquiry.setTime(null);

        } else if (dto.getEnquiryType() == EnquiryType.BOOK_A_TOUR) {
        	
        	if (dto.getSolutionName() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solution name is required for book a tour enquiry");
            }
        	
        	Solutions solution = solutionsRepo.findByNameAndLetsWorkCentreAndCompanyId(dto.getSolutionName(), dto.getLetsWorkCentre(), dto.getCompanyId());
        	
        	if(solution==null) {
        		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solution not found");
        	}
        	
            enquiry.setSolutions(solution);

            if (dto.getDate() == null || dto.getTime() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date and time are required for BOOK_A_TOUR enquiry"
                );
            }

            enquiry.setDate(dto.getDate());
            enquiry.setTime(dto.getTime());
        }

        enquiry.setCompanyId(dto.getCompanyId());
        enquiry.setCreateDate(new Date());
        enquiry.setUpdateDate(new Date());

        return enquiryRepository.save(enquiry);
    }

    @Override
    public PaginatedResponseDto getEnquiriesPaginated(
            String companyId,
            String name,
            String email,
            String phone,
            String letsWorkCentre,
            String city,
            String state,
            String search,
            Date fromDate,
            Date toDate,
            EnquiryType enquiryType,
            EnquiryStatus enquiryStatus,
            String solutionName,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("id").descending()
        );

        if (search != null && search.trim().isEmpty()) {
            search = null;
        }

        Page<Enquiry> resultPage = enquiryRepository.findByFilters(
                companyId,
                name,
                email,
                phone,
                letsWorkCentre,
                city,
                state,
                search,
                fromDate,
                toDate,
                enquiryType,
                enquiryStatus,
                solutionName,
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

	@Override
	public String updateEnquiryStatus(Long enquiryId) {
		
		Enquiry enquiry = enquiryRepository.findById(enquiryId)
	            .orElseThrow(() -> new RuntimeException("Enquiry not found"));

	    EnquiryStatus currentStatus = enquiry.getEnquiryStatus();

	    if (currentStatus == EnquiryStatus.RAISED) {
	        enquiry.setEnquiryStatus(EnquiryStatus.IN_PROGRESS);
	    } else if (currentStatus == EnquiryStatus.IN_PROGRESS) {
	        enquiry.setEnquiryStatus(EnquiryStatus.RESOLVED);
	    } else {
	        throw new RuntimeException("Enquiry is already resolved");
	    }

	    enquiryRepository.save(enquiry);
	    return "Status updated successfully";
	    
	}
	
}
