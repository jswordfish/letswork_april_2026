package com.LetsWork.CRM.serviceImpl;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.EscalationTimeAndPercentage;
import com.LetsWork.CRM.entities.LandLord;
import com.LetsWork.CRM.repo.LandLordRepository;
import com.LetsWork.CRM.service.LandLordService;


@Service
@Transactional
public class LandLordServiceImpl implements LandLordService {
	
	@Autowired
	LandLordRepository repo;
	
	@Autowired
	S3Service s3Service;
	
	String bucketName = "myapp-bucket-1758037822620";

	@Override
	public LandLord saveOrUpdate(LandLord incoming, MultipartFile panFile, MultipartFile aadharFile,
            MultipartFile gstFile, MultipartFile agreementFile) throws Exception {
		// enforce business key
		if (incoming.getGstNumber() == null || incoming.getGstNumber().trim().isEmpty()) {
		throw new IllegalArgumentException("gstNumber is required (business key).");
		}
		
		Optional<LandLord> existingOpt = repo.findByGstNumber(incoming.getGstNumber().trim());
		LandLord entity;
		
		if (existingOpt.isPresent()) {
		// update existing
		entity = existingOpt.get();
		
		// update all scalar fields except id and gstNumber
		entity.setName(incoming.getName());
		entity.setSpocFirstName(incoming.getSpocFirstName());
		entity.setSpocLastName(incoming.getSpocLastName());
		entity.setSpocEmail(incoming.getSpocEmail());
		entity.setAadharNumber(incoming.getAadharNumber());
		entity.setSpocAadharNumber(incoming.getSpocAadharNumber());
		entity.setPanNumber(incoming.getPanNumber());
		entity.setSpocPanNumber(incoming.getSpocPanNumber());
		entity.setTinNumber(incoming.getTinNumber());
		entity.setCinNumber(incoming.getCinNumber());
		entity.setDepositAmount(incoming.getDepositAmount());
		entity.setTenure(incoming.getTenure());
		entity.setRent(incoming.getRent());
		entity.setRemarks(incoming.getRemarks());
		entity.setAgreementType(incoming.getAgreementType());
		entity.setCompanyId(incoming.getCompanyId());
		
		// Replace child list
		entity.getTimeAndPercentage().clear();
		if (incoming.getTimeAndPercentage() != null) {
		for (EscalationTimeAndPercentage child : incoming.getTimeAndPercentage()) {
		child.setLandLord(entity); // important for bidirectional mapping
		entity.getTimeAndPercentage().add(child);
		}
		}
		
		} else {
		// new landlord
		entity = incoming;
		
		if (entity.getTimeAndPercentage() != null) {
		for (EscalationTimeAndPercentage child : entity.getTimeAndPercentage()) {
		child.setLandLord(entity);
		}
		}
		}
		
		// handle file uploads
		String landlordName = entity.getName() != null ? entity.getName() : entity.getGstNumber();
		
		if (panFile != null && !panFile.isEmpty()) {
			
			if (entity.getPanCardS3Path() != null) {
		        s3Service.deleteLandlordDocument(bucketName, entity.getPanCardS3Path());
		    }
			
		File temp = File.createTempFile("pan-", Objects.requireNonNull(panFile.getOriginalFilename()));
		panFile.transferTo(temp);
		String url = s3Service.uploadLandlordDocument(bucketName, entity.getCompanyId(), landlordName, "pan", panFile.getOriginalFilename(), temp);
		entity.setPanCardS3Path(url);
		temp.delete();
		}
		
		if (aadharFile != null && !aadharFile.isEmpty()) {
			
			if (entity.getAadharCardS3Path() != null) {
		        s3Service.deleteLandlordDocument(bucketName, entity.getAadharCardS3Path());
		    }
			
		File temp = File.createTempFile("aadhar-", Objects.requireNonNull(aadharFile.getOriginalFilename()));
		aadharFile.transferTo(temp);
		String url = s3Service.uploadLandlordDocument(bucketName, entity.getCompanyId(), landlordName, "aadhar", aadharFile.getOriginalFilename(), temp);
		entity.setAadharCardS3Path(url);
		temp.delete();
		}
		
		if (gstFile != null && !gstFile.isEmpty()) {
			
			if (entity.getGstCertificateS3Path() != null) {
		        s3Service.deleteLandlordDocument(bucketName, entity.getGstCertificateS3Path());
		    }
			
		File temp = File.createTempFile("gst-", Objects.requireNonNull(gstFile.getOriginalFilename()));
		gstFile.transferTo(temp);
		String url = s3Service.uploadLandlordDocument(bucketName, entity.getCompanyId(), landlordName, "gst", gstFile.getOriginalFilename(), temp);
		entity.setGstCertificateS3Path(url);
		temp.delete();
		}
		
		if (agreementFile != null && !agreementFile.isEmpty()) {
			
			if (entity.getAgreementFileS3Path() != null) {
		        s3Service.deleteLandlordDocument(bucketName, entity.getAgreementFileS3Path());
		    }
			
		File temp = File.createTempFile("agreement-", Objects.requireNonNull(agreementFile.getOriginalFilename()));
		agreementFile.transferTo(temp);
		String url = s3Service.uploadLandlordDocument(bucketName, entity.getCompanyId(), landlordName, "agreement", agreementFile.getOriginalFilename(), temp);
		entity.setAgreementFileS3Path(url);
		temp.delete();
		}
		
		// save parent and children in one transaction
		return repo.save(entity);
	}

	@Override
	public PaginatedResponseDto listAll(int page, String companyId) {
		// TODO Auto-generated method stub
		if (page < 0) page = 0;

	    int size = 10; // 👈 fixed size
	    Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

	    Page<LandLord> pageRes;
	    if (companyId == null || companyId.trim().isEmpty()) {
	        pageRes = repo.findAll(pageable);
	    } else {
	        pageRes = repo.findByCompanyId(companyId, pageable);
	    }

	    PaginatedResponseDto dto = new PaginatedResponseDto();
	    dto.setSelectedPage(pageRes.getNumber());
	    dto.setTotalNumberOfPages(pageRes.getTotalPages());
	    dto.setTotalNumberOfRecords((int) pageRes.getTotalElements());
	    dto.setRecordsFrom(pageRes.getNumber() * pageRes.getSize() + 1);
	    dto.setRecordsTo(pageRes.getNumber() * pageRes.getSize() + pageRes.getNumberOfElements());
	    dto.setList(pageRes.getContent());
	    return dto;
	}

	@Override
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		repo.deleteById(id);
	}

	@Override
	public Optional<LandLord> findByGstNumber(String gstNumber) {
		// TODO Auto-generated method stub
		return repo.findByGstNumber(gstNumber);
	}

}
