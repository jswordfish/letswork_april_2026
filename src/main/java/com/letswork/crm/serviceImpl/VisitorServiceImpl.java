package com.letswork.crm.serviceImpl;

import java.io.File;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.entities.Visitor;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.NewUserRegisterRepository;
import com.letswork.crm.repo.VisitorRepository;
import com.letswork.crm.service.QRCodeService;
import com.letswork.crm.service.TenantService;
import com.letswork.crm.service.VisitorService;
import com.letswork.crm.service.WhatsAppService;

import lombok.RequiredArgsConstructor;



@Service
@Transactional
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService {
	
	@Autowired
	VisitorRepository repo;
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	LetsWorkCentreRepository letsWorkCentreRepo;
	
	@Autowired
	NewUserRegisterRepository userRepo;
	
	@Autowired
	WhatsAppService whatsAppService;
	
	private final QRCodeService qrService;
    private final S3Service s3Service;
    
	ModelMapper mapper = new ModelMapper();

	@Override
	public String saveOrUpdate(Visitor visitor) {

	    Tenant tenant =
	            tenantService.findTenantByCompanyId(visitor.getCompanyId());

	    if (tenant == null) {
	        throw new RuntimeException(
	                "Invalid companyId - " + visitor.getCompanyId()
	        );
	    }
	    
		LetsWorkCentre centre = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(visitor.getLetsWorkCentre(), visitor.getCompanyId(), visitor.getCity(), visitor.getState());
		
		if(centre==null) {
			throw new RuntimeException("This LetsWorkCentre does not exists");
		}
		
		NewUserRegister user = userRepo.findByEmailAndCompanyId(visitor.getEmail(), visitor.getCompanyId()).orElseThrow(() -> new RuntimeException("This user does not exists"));

	    Visitor existing =
	            repo.findByCompanyIdAndEmailOfVisitorAndVisitDate(
	                    visitor.getCompanyId(),
	                    visitor.getEmailOfVisitor(),
	                    visitor.getVisitDate()
	            );

	    Visitor saved;

	    if (existing != null) {

	        mapper.map(visitor, existing);
	        existing.setUpdateDate(new Date());

	        saved = repo.save(existing);
	        return "visitor updated";

	    } else {

	        visitor.setBookingCode(UUID.randomUUID().toString());
	        visitor.setCreateDate(new Date());
	        visitor.setUpdateDate(new Date());

	        saved = repo.save(visitor);

	        generateAndUploadQr(saved);
	        try {
				whatsAppService.sendBookingQRCode("918652769926", "C:\\Users\\hp\\Desktop\\Dhruv2025\\Images\\QR.png");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return "visitor created";
	    }
	}
	
	private void generateAndUploadQr(Visitor visitor) {

	    try {
	        String qrPath =
	                qrService.generateQRCodeWithBookingCodeRGB(
	                        "VISITOR|" + visitor.getBookingCode()
	                );

	        File qrFile = new File(qrPath);

	        String s3Path =
	                s3Service.uploadVisitorQrCode(
	                        "letsworkcentres",
	                        visitor.getCompanyId(),
	                        visitor.getEmailOfVisitor(),
	                        visitor.getBookingCode(),
	                        qrFile
	                );

	        visitor.setQrS3Path(s3Path);
	        repo.save(visitor);

	        qrFile.delete();

	    } catch (Exception e) {
	        throw new RuntimeException(
	                "QR generation/upload failed", e
	        );
	    }
	}
	
	@Override
	public List<Visitor> filter(
	        String companyId,
	        String name,
	        String email,
	        String emailOfVisitor,
	        LocalDate visitDate,
	        String centre,
	        String city,
	        String state,
	        String type
	) {

	    List<Visitor> visitors =
	            repo.filter(
	                    companyId,
	                    name,
	                    email,
	                    emailOfVisitor,
	                    visitDate,
	                    centre,
	                    city,
	                    state
	            );

	    if (type == null || type.trim().isEmpty()) {
	        return visitors;
	    }

	    LocalDate today = LocalDate.now();

	    if ("upcoming".equalsIgnoreCase(type)) {

	        return visitors.stream()
	                .filter(v ->
	                        v.getVisitDate() != null &&
	                        !v.getVisitDate().isBefore(today)
	                )
	                .collect(Collectors.toList());

	    }

	    if ("history".equalsIgnoreCase(type)) {

	        return visitors.stream()
	                .filter(v ->
	                        v.getVisitDate() != null &&
	                        v.getVisitDate().isBefore(today)
	                )
	                .collect(Collectors.toList());
	    }

	    return visitors;
	}

	@Override
	public List<Visitor> viewByDate(LocalDate visitDate) {
		// TODO Auto-generated method stub
		return repo.findByVisitDate(visitDate);
	}

	@Override
	public String deleteVisitor(Visitor visitor) {
		// TODO Auto-generated method stub
		
		Visitor vit = repo.findByNameAndEmail(visitor.getName(), visitor.getEmail());
		if(vit!=null) {
		repo.delete(visitor);
		return "record deleted";
		}
		else return "No visitor found";
		
	}
	
	private static final int PAGE_SIZE = 10; 

    @Override
    public PaginatedResponseDto viewByDate(LocalDate visitDate, String companyId, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("name").ascending());
        Page<Visitor> visitorPage = repo.findByVisitDate(visitDate, companyId, pageable);

        return buildPaginatedResponse(visitorPage, page);
    }

    
    private PaginatedResponseDto buildPaginatedResponse(Page<Visitor> visitorPage, int page) {
        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom((page * PAGE_SIZE) + 1);
        response.setRecordsTo(Math.min((page + 1) * PAGE_SIZE, (int) visitorPage.getTotalElements()));
        response.setTotalNumberOfRecords((int) visitorPage.getTotalElements());
        response.setTotalNumberOfPages(visitorPage.getTotalPages());
        response.setSelectedPage(page + 1);
        response.setList(visitorPage.getContent());
        return response;
    }

}
