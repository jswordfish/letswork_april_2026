package com.letswork.crm.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.dtos.SolutionsDto;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.SolutionType;
import com.letswork.crm.entities.Solutions;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.SolutionTypeRepository;
import com.letswork.crm.repo.SolutionsRepository;
import com.letswork.crm.service.SolutionsService;
import com.letswork.crm.service.TenantService;


@Service
@Transactional
public class SolutionsServiceImpl implements SolutionsService{
	
	@Autowired
    private SolutionsRepository repo;

    @Autowired
    private TenantService tenantService;
    
    @Autowired
    LetsWorkCentreRepository letsWorkCentreRepo;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    SolutionTypeRepository solutionTypeRepo;

    
    private String bucketName = "letsworkcentres";

    @Override
    public String saveOrUpdate(
            SolutionsDto dto,
            MultipartFile image
    ) throws IOException {

        // ✅ Validate tenant
        Tenant tenant = tenantService.findTenantByCompanyId(dto.getCompanyId());

        if (tenant == null) {
            throw new RuntimeException("Invalid companyId - " + dto.getCompanyId());
        }

        // ✅ Validate centre
        LetsWorkCentre centre =
                letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(
                        dto.getLetsWorkCentre(),
                        dto.getCompanyId(),
                        dto.getCity(),
                        dto.getState()
                );

        if (centre == null) {
            throw new RuntimeException("This LetsWorkCentre does not exist");
        }

        // ✅ Validate solutionTypeId
        if (dto.getSolutionTypeId() == null) {
            throw new RuntimeException("solutionTypeId is required");
        }

        SolutionType solutionType = solutionTypeRepo.findById(dto.getSolutionTypeId())
                .orElseThrow(() -> new RuntimeException("Invalid solutionTypeId"));

        // ✅ Check existing
        Solutions existing =
                repo.findByNameAndLetsWorkCentreAndCompanyId(
                        dto.getName(),
                        dto.getLetsWorkCentre(),
                        dto.getCompanyId()
                );

        Solutions solution;
        boolean isUpdate = (existing != null);

        if (isUpdate) {

            solution = existing;

            solution.setName(dto.getName());
            solution.setPrice(dto.getPrice());
            solution.setLetsWorkCentre(dto.getLetsWorkCentre());
            solution.setCity(dto.getCity());
            solution.setState(dto.getState());
            solution.setAmenities(dto.getAmenities());

            solution.setSolutionType(solutionType); 

            solution.setUpdateDate(new Date());

        } else {

            solution = new Solutions();

            solution.setName(dto.getName());
            solution.setPrice(dto.getPrice());
            solution.setLetsWorkCentre(dto.getLetsWorkCentre());
            solution.setCity(dto.getCity());
            solution.setState(dto.getState());
            solution.setAmenities(dto.getAmenities());

            solution.setSolutionType(solutionType); 

            solution.setCompanyId(dto.getCompanyId());
            solution.setCreateDate(new Date());
            solution.setUpdateDate(new Date());
        }

        Solutions saved = repo.save(solution);

        if (image != null && !image.isEmpty()) {

            File tempFile = File.createTempFile(
                    "solution_",
                    image.getOriginalFilename()
            );

            image.transferTo(tempFile);

            String s3Key =
                    s3Service.uploadSolutionImage(
                            bucketName,
                            saved.getCompanyId(),
                            saved.getLetsWorkCentre(),
                            saved.getName(),
                            image.getOriginalFilename(),
                            tempFile
                    );

            saved.setS3Path(s3Key);
            repo.save(saved);

            tempFile.delete();
        }

        return isUpdate ? "solution updated" : "solution created";
    }

//	@Override
//	public List<Solutions> findByCompanyId(String companyId) {
//		// TODO Auto-generated method stub
//		return repo.findByCompanyId(companyId);
//	}
	
	@Override
	public PaginatedResponseDto getPaginated(
	        String companyId,
	        String letsWorkCentre,
	        int page,
	        int size
	) {

	    Pageable pageable = PageRequest.of(
	            page,
	            size,
	            Sort.by("id").descending()
	    );

	    Page<Solutions> resultPage;

	    if (letsWorkCentre != null) {
	        resultPage = repo.findByLetsWorkCentreAndCompanyId(
	                letsWorkCentre,
	                companyId,
	                pageable
	        );
	    } else {
	        resultPage = repo.findByCompanyId(
	                companyId,
	                pageable
	        );
	    }

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

//	@Override
//	public List<Solutions> findByLetsWorkCentreAndCompanyId(String letsWorkCentre, String companyId) {
//		// TODO Auto-generated method stub
//		return repo.findByLetsWorkCentreAndCompanyId(letsWorkCentre, companyId);
//	}

	@Override
	public Solutions findByNameAndLetsWorkCentreAndCompanyId(String name, String letsWorkCentre, String companyId) {
		// TODO Auto-generated method stub
		return repo.findByNameAndLetsWorkCentreAndCompanyId(name, letsWorkCentre, companyId);
	}

}
