package com.letswork.crm.serviceImpl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Category;
import com.letswork.crm.entities.Greviance;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.entities.SubCategory;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.CategoryType;
import com.letswork.crm.enums.GrevianceStatus;
import com.letswork.crm.repo.CategoryRepository;
import com.letswork.crm.repo.GrevianceRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.NewUserRegisterRepository;
import com.letswork.crm.repo.SubCategoryRepository;
import com.letswork.crm.service.GrevianceService;
import com.letswork.crm.service.TenantService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class GrevianceServiceImpl implements GrevianceService {

    private final GrevianceRepository grevianceRepo;
    private final NewUserRegisterRepository userRepo;
    private final CategoryRepository categoryRepo;
    private final SubCategoryRepository subCategoryRepo;
    
    @Autowired
	TenantService tenantService;
	
	@Autowired
	LetsWorkCentreRepository letsWorkCentreRepo;
	
	@Autowired
	S3Service s3Service;

    @Override
    public Greviance saveGreviance(Greviance greviance, MultipartFile image) {

        Tenant tenant =
                tenantService.findTenantByCompanyId(greviance.getCompanyId());

        if (tenant == null) {
            throw new RuntimeException("CompanyId invalid - " + greviance.getCompanyId());
        }

        LetsWorkCentre centre =
                letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(
                        greviance.getLetsWorkCentre(),
                        greviance.getCompanyId(),
                        greviance.getCity(),
                        greviance.getState()
                );

        if (centre == null) {
            throw new RuntimeException("This LetsWorkCentre does not exist");
        }

        NewUserRegister user = userRepo.findById(
                greviance.getClientId()
        ).orElseThrow(() ->
                new RuntimeException("User not found for given id")
        );

        Category category =
                categoryRepo.findByNameAndCompanyIdAndCategoryType(
                        greviance.getCategory(),
                        greviance.getCompanyId(),
                        CategoryType.GREVIANCE
                );

        if (category == null) {
            throw new RuntimeException("Invalid category");
        }

        SubCategory subCategory =
                subCategoryRepo.findFirstByNameAndCompanyIdAndCategoryTypeOrderByCreateDateDesc(
                        greviance.getSubCategory(),
                        greviance.getCompanyId(),
                        CategoryType.GREVIANCE
                );

        if (subCategory == null) {
            throw new RuntimeException("Invalid sub-category");
        }

        // ✅ Upload image if present
        if (image != null && !image.isEmpty()) {
            String s3Key =
                    s3Service.uploadGrevianceImage(
                            "letsworkcentres",
                            greviance.getCompanyId(),
                            user.getEmail(),
                            image
                    );
            greviance.setImageS3Key(s3Key);
        }

        greviance.setGrevianceStatus(GrevianceStatus.RAISED);

        return grevianceRepo.save(greviance);
    }

    @Override
    public PaginatedResponseDto getGreviances(
            String companyId,
            Long clientId,
            String centre,
            String city,
            String state,
            String category,
            String subCategory,
            GrevianceStatus status,
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(page, size, Sort.by("createDate").descending());

        Page<Greviance> greviancePage =
                grevianceRepo.filter(
                        companyId,
                        clientId,
                        centre,
                        city,
                        state,
                        category,
                        subCategory,
                        status,
                        pageable
                );

        PaginatedResponseDto dto = new PaginatedResponseDto();
        dto.setSelectedPage(page);
        dto.setTotalNumberOfPages(greviancePage.getTotalPages());
        dto.setTotalNumberOfRecords((int) greviancePage.getTotalElements());

        dto.setRecordsFrom(page * size + 1);
        dto.setRecordsTo(
                Math.min((page + 1) * size, dto.getTotalNumberOfRecords())
        );

        dto.setList(greviancePage.getContent());

        return dto;
    }
    
    @Override
    public Greviance updateGrevianceStatus(
            Long grevianceId,
            GrevianceStatus status,
            String companyId
    ) {

        Greviance greviance = grevianceRepo
                .findByIdAndCompanyId(grevianceId, companyId)
                .orElseThrow(() ->
                        new RuntimeException("Greviance not found"));

        if (greviance.getGrevianceStatus() == status) {
            throw new RuntimeException("Greviance already in this status");
        }

        if (greviance.getGrevianceStatus() == GrevianceStatus.COMPLETED) {
            throw new RuntimeException("Completed greviance cannot be updated");
        }

        greviance.setGrevianceStatus(status);

        return grevianceRepo.save(greviance);
    }
    
}
