package com.letswork.crm.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.Solutions;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.LetsWorkCentreRepository;
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

    
    private String bucketName = "letsworkcentres";

	@Override
	public String saveOrUpdate(
	        Solutions solution,
	        MultipartFile image
	) throws IOException {

	    // ✅ Validate company
	    Tenant tenant =
	            tenantService.findTenantByCompanyId(
	                    solution.getCompanyId()
	            );

	    if (tenant == null) {
	        throw new RuntimeException(
	                "Invalid companyId - " + solution.getCompanyId()
	        );
	    }
	    
		LetsWorkCentre centre = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(solution.getLetsWorkCentre(), solution.getCompanyId(), solution.getCity(), solution.getState());
		
		if(centre==null) {
			throw new RuntimeException("This LetsWorkCentre does not exists");
		}

	    // ✅ Check existing by business key
	    Solutions existing =
	            repo.findByNameAndLetsWorkCentreAndCompanyId(
	                    solution.getName(),
	                    solution.getLetsWorkCentre(),
	                    solution.getCompanyId()
	            );

	    Solutions saved;

	    if (existing != null) {

	        // ✅ Safe merge (ID is protected)
	        objectMapper.updateValue(existing, solution);

	        existing.setUpdateDate(new Date());

	        saved = repo.save(existing);

	    } else {

	        solution.setCreateDate(new Date());
	        solution.setUpdateDate(new Date());

	        saved = repo.save(solution);
	    }

	    // ✅ Upload image (AFTER save)
	    if (image != null && !image.isEmpty()) {

	        File tempFile =
	                File.createTempFile(
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

	        saved.setS3Path(s3Key); // store KEY, not URL
	        repo.save(saved);

	        tempFile.delete();
	    }

	    return existing != null
	            ? "solution updated"
	            : "solution created";
	}

	@Override
	public List<Solutions> findByCompanyId(String companyId) {
		// TODO Auto-generated method stub
		return repo.findByCompanyId(companyId);
	}

	@Override
	public List<Solutions> findByLetsWorkCentreAndCompanyId(String letsWorkCentre, String companyId) {
		// TODO Auto-generated method stub
		return repo.findByLetsWorkCentreAndCompanyId(letsWorkCentre, companyId);
	}

	@Override
	public Solutions findByNameAndLetsWorkCentreAndCompanyId(String name, String letsWorkCentre, String companyId) {
		// TODO Auto-generated method stub
		return repo.findByNameAndLetsWorkCentreAndCompanyId(name, letsWorkCentre, companyId);
	}

}
