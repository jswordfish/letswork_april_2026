package com.letswork.crm.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.GrevianceResponseDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Category;
import com.letswork.crm.entities.Greviance;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.entities.SubCategory;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.CategoryType;
import com.letswork.crm.enums.GrevianceStatus;
import com.letswork.crm.repo.CategoryRepository;
import com.letswork.crm.repo.GrevianceRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
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
	private LetsWorkClientRepository letsWorkClientRepo;
	
	@Autowired
	S3Service s3Service;

    @Override
    public Greviance saveGreviance(Greviance greviance, MultipartFile image) {

        Tenant tenant =
                tenantService.findTenantByCompanyId(greviance.getCompanyId());

        if (tenant == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CompanyId invalid - " + greviance.getCompanyId());
        }

        LetsWorkCentre centre =
                letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(
                        greviance.getLetsWorkCentre(),
                        greviance.getCompanyId(),
                        greviance.getCity(),
                        greviance.getState()
                );

        if (centre == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This LetsWorkCentre does not exist");
        }

        NewUserRegister user = userRepo.findById(
                greviance.getUserId()
        ).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "User not found for given id")
        );
        
        LetsWorkClient client = letsWorkClientRepo.findById(
                greviance.getClientId()
        ).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client company not found for given id")
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
            Long userId,
            String centre,
            String city,
            String state,
            String category,
            String subCategory,
            GrevianceStatus status,
            String search,
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(page, size, Sort.by("id").descending());

        if (search != null && search.trim().isEmpty()) {
            search = null;
        }

        Page<Greviance> greviancePage =
                grevianceRepo.filter(
                        companyId,
                        clientId,
                        userId,
                        centre,
                        city,
                        state,
                        category,
                        subCategory,
                        status,
                        search,
                        pageable
                );

        List<GrevianceResponseDto> responseList =
                greviancePage.getContent().stream().map(g -> {

                    GrevianceResponseDto response = new GrevianceResponseDto();

                    response.setId(g.getId());
                    response.setClientId(g.getClientId());
                    response.setUserId(g.getUserId());
                    response.setLetsWorkCentre(g.getLetsWorkCentre());
                    response.setCity(g.getCity());
                    response.setState(g.getState());
                    response.setCategory(g.getCategory());
                    response.setSubCategory(g.getSubCategory());
                    response.setIssue(g.getIssue());
                    response.setGrevianceStatus(g.getGrevianceStatus());
                    response.setImageS3Key(g.getImageS3Key());

                    if (g.getClientId() != null) {
                        LetsWorkClient client =
                                letsWorkClientRepo.findById(g.getClientId()).orElse(null);
                        response.setClient(client);
                    }

                    if (g.getUserId() != null) {
                        NewUserRegister user =
                        		userRepo.findById(g.getUserId()).orElse(null);
                        response.setUser(user);
                    }

                    return response;
                }).collect(Collectors.toList());

        PaginatedResponseDto dto = new PaginatedResponseDto();
        dto.setSelectedPage(page);
        dto.setTotalNumberOfPages(greviancePage.getTotalPages());
        dto.setTotalNumberOfRecords((int) greviancePage.getTotalElements());
        dto.setRecordsFrom(page * size + 1);
        dto.setRecordsTo(
                Math.min((page + 1) * size, dto.getTotalNumberOfRecords())
        );
        dto.setList(responseList);

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
