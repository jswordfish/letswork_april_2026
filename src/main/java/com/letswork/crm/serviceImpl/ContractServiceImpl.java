package com.letswork.crm.serviceImpl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letswork.crm.dtos.AgreementDto;
import com.letswork.crm.dtos.BulkSeatAssignmentRequestContract;
import com.letswork.crm.dtos.ContractDeleteDto;
import com.letswork.crm.dtos.ConvertedContractDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Contract;
import com.letswork.crm.entities.ContractSeatMapping;
import com.letswork.crm.entities.Lead;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.ContractStatus;
import com.letswork.crm.enums.DateFilterType;
import com.letswork.crm.enums.LeadStatus;
import com.letswork.crm.repo.ContractRepository;
import com.letswork.crm.repo.ContractSeatMappingRepository;
import com.letswork.crm.repo.LeadRepo;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.service.ContractSeatMappingService;
import com.letswork.crm.service.ContractService;
import com.letswork.crm.service.LeadService;
import com.letswork.crm.service.NewUserRegisterService;
import com.letswork.crm.service.TenantService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ContractServiceImpl implements ContractService {

    @Autowired
    private ContractRepository contractRepo;
    
    @Autowired
    private ContractSeatMappingService mappingService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private LetsWorkClientRepository letsWorkClientRepo;
    
    @Autowired
    ContractDocumentService contractDocumentService;
    
    @Autowired
    MailJetOtpService mailService;
    
    @Autowired
    S3Service s3Service;
    
    @Autowired
    LeadRepo leadRepo;
    
    @Autowired
    LeadService leadService;
    
    @Autowired
    NewUserRegisterService newUserRegisterService;
    
    @Autowired
    ContractSeatMappingRepository seatMappingRepo;
    
    @Autowired
    private ObjectMapper objectMapper;

    private final ModelMapper mapper = new ModelMapper();

    @Override
    public Contract saveOrUpdate(Contract contract) {

        Tenant tenant = tenantService.findTenantByCompanyId(contract.getCompanyId());
        if (tenant == null) {
            throw new RuntimeException("Invalid companyId");
        }

        LetsWorkClient client = letsWorkClientRepo
                .findByIdAndCompanyId(contract.getLetsWorkClient().getId(), contract.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Invalid LetsWorkClient"));

        contract.setLetsWorkClient(client);

        if (contract.getId() != null) {

            Contract existing = contractRepo
                    .findByIdAndCompanyId(contract.getId(), contract.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Contract not found"));

            contract.setCreateDate(existing.getCreateDate());
            contract.setUpdateDate(new Date());

            mapper.map(contract, existing);
            Contract saved = contractRepo.save(existing);

            byte[] pdfBytes = contractDocumentService.generateAgreementPdf(saved);

            String s3Key = s3Service.uploadContractAgreementPdf(
            		"letsworkcentres",
                    saved.getCompanyId(),
                    saved.getId(),
                    pdfBytes
            );

            saved.setAgreementS3KeyName(s3Key);
            return contractRepo.save(saved);
        }

        else {
        	
            contract.setCreateDate(new Date());
            
            contract.setActualEndDate(contract.getEndDate());
            
            contract.setActive(true);

            Contract saved = contractRepo.save(contract);

            byte[] pdfBytes = contractDocumentService.generateAgreementPdf(saved);

            String s3Key = s3Service.uploadContractAgreementPdf(
            		"letsworkcentres",
                    saved.getCompanyId(),
                    saved.getId(),
                    pdfBytes
            );

            saved.setAgreementS3KeyName(s3Key);

            return contractRepo.save(saved);
        }
    }
    
    @Override
    @Transactional
    public ConvertedContractDto saveOrUpdateConverted(ConvertedContractDto dto,
            MultipartFile agreement,
            BulkSeatAssignmentRequestContract assignment) {

        Tenant tenant = tenantService.findTenantByCompanyId(dto.getContract().getCompanyId());
        if (tenant == null) {
            throw new RuntimeException("Invalid companyId");
        }

        if (dto.getContract().getId() != null) {

            Contract existing = contractRepo
                    .findByIdAndCompanyId(dto.getContract().getId(), dto.getContract().getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Contract not found"));

            dto.getContract().setCreateDate(existing.getCreateDate());
            dto.getContract().setUpdateDate(new Date());

            mapper.map(dto.getContract(), existing);
            Contract saved = contractRepo.save(existing);

            uploadAgreementIfPresent(saved, agreement);

            ConvertedContractDto response = new ConvertedContractDto();

            if (dto.getNewUserRegister() != null) {
                NewUserRegister user = newUserRegisterService.saveOrUpdateManually(dto.getNewUserRegister());
                response.setNewUserRegister(user);
            } else {
                response.setNewUserRegister(null);
            }

            Contract contract = contractRepo.save(saved);

            response.setContract(contract);

            assignment.setContractId(contract.getId());
            mappingService.assignMultipleSeatsToContract(assignment);

            return response;
        }

        else {

            ConvertedContractDto response = new ConvertedContractDto();

            NewUserRegister savedUser = null;

            if (dto.getNewUserRegister() != null) {

                savedUser = newUserRegisterService.saveOrUpdateManually(dto.getNewUserRegister());

                response.setNewUserRegister(savedUser);

                LetsWorkClient client = letsWorkClientRepo
                        .findByClientCompanyNameAndCompanyId(
                                dto.getNewUserRegister().getClientCompanyName(),
                                dto.getContract().getCompanyId()
                        )
                        .stream()
                        .findFirst()
                        .orElse(null);

                if (client == null) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "LetsWorkClient was not created for company : " + savedUser.getClientCompanyName()
                    );
                }

                dto.getContract().setLetsWorkClient(client);

            } else {

                response.setNewUserRegister(null);

                if (dto.getContract().getLetsWorkClient() == null
                        || dto.getContract().getLetsWorkClient().getId() == null) {

                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "LetsWorkClient is required"
                    );
                }

                LetsWorkClient client = letsWorkClientRepo
                        .findByIdAndCompanyId(
                                dto.getContract().getLetsWorkClient().getId(),
                                dto.getContract().getCompanyId()
                        )
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Invalid LetsWorkClient"
                        ));

                dto.getContract().setLetsWorkClient(client);
            }

            dto.getContract().setCreateDate(new Date());
            dto.getContract().setActive(true);
            dto.getContract().setActualEndDate(dto.getContract().getEndDate());

            Contract saved = contractRepo.save(dto.getContract());

            if (agreement != null) {
                uploadAgreementIfPresent(saved, agreement);
            } else {
                byte[] pdfBytes = contractDocumentService.generateAgreementPdf(saved);

                String s3Key = s3Service.uploadContractAgreementPdf(
                        "letsworkcentres",
                        saved.getCompanyId(),
                        saved.getId(),
                        pdfBytes
                );

                saved.setAgreementS3KeyName(s3Key);
            }

            saved = contractRepo.save(saved);

            response.setContract(saved);

            assignment.setContractId(saved.getId());
            mappingService.assignMultipleSeatsToContract(assignment);

            String link = generateLink(saved);

            mailService.sendOnboardingEmail(saved.getId(), saved.getCompanyId(),
                    saved.getLetsWorkClient().getClientCompanyName(),
                    link, saved.getLetsWorkClient().getEmail());

            return response;
        }
    }
    
    private String generateLink(Contract contract) {
    	
		String baseUrl = "https://letsworkadmin.vercel.app/onboarding?letsWorkClientId="; //test
		
//		String baseUrl = "https://letsworkapp.in/onboarding?letsWorkClientId="; //live
    	
		if(contract==null) {
    		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contract not found");
    	}
    	
    	Long letsWorkClientId = contract.getLetsWorkClient().getId();
    	
    	return baseUrl+letsWorkClientId;
    	
    }
    
    private void uploadAgreementIfPresent(
            Contract contract,
            MultipartFile agreement
    ) {

        if (agreement == null || agreement.isEmpty()) {
            return;
        }

        try {

            String s3Key =
                    s3Service.uploadContractAgreementPdf(
                            "letsworkcentres",
                            contract.getCompanyId(),
                            contract.getId(),
                            agreement.getBytes()
                    );

            contract.setAgreementS3KeyName(s3Key);
            
            contractRepo.save(contract);

        } catch (Exception e) {

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to upload agreement",
                    e
            );
        }
    }

    @Override
    public PaginatedResponseDto getPaginated(
            String companyId,
            Long letsWorkClientId,
            ContractStatus status,
            LocalDate fromDate,
            LocalDate toDate,
            DateFilterType dateFilterType,
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("id").descending()
                );

        Page<Contract> resultPage;

        if (dateFilterType == DateFilterType.FILTER_ON_START_DATE) {

            resultPage =
                    contractRepo.filterOnStartDate(
                            companyId,
                            letsWorkClientId,
                            status,
                            fromDate,
                            toDate,
                            pageable
                    );

        } else if (dateFilterType == DateFilterType.FILTER_ON_ACTUAL_END_DATE) {

            resultPage =
                    contractRepo.filterOnActualEndDate(
                            companyId,
                            letsWorkClientId,
                            status,
                            fromDate,
                            toDate,
                            pageable
                    );

        } else {

            resultPage =
                    contractRepo.filter(
                            companyId,
                            letsWorkClientId,
                            status,
                            pageable
                    );
        }

        PaginatedResponseDto dto =
                new PaginatedResponseDto();

        dto.setSelectedPage(page);

        dto.setTotalNumberOfRecords(
                (int) resultPage.getTotalElements()
        );

        dto.setTotalNumberOfPages(
                resultPage.getTotalPages()
        );

        dto.setRecordsFrom(
                resultPage.getTotalElements() == 0
                        ? 0
                        : page * size + 1
        );

        dto.setRecordsTo(
                Math.min(
                        (page + 1) * size,
                        (int) resultPage.getTotalElements()
                )
        );

        dto.setList(
                resultPage.getContent()
        );

        return dto;
    }


    @Override
    public String sendAgreementDefaultOnMail(AgreementDto dto) {

        try {

        	Lead lead = leadRepo.findFirstByEmailAndCompanyIdOrderByIdDesc(
        	        dto.getLeadEmail(),
        	        dto.getCompanyId()
        	).orElse(null);

            if (lead == null) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Lead not found with email : "
                                + dto.getLeadEmail()
                );
            }

            String agreementJson =
                    objectMapper.writeValueAsString(dto);

            lead.setAgreementJson(
                    agreementJson
            );

            leadRepo.save(lead);

            byte[] agreementPdf =
                    contractDocumentService
                            .generateAgreementPdfFromDto(dto);

            mailService.sendAgreementEmail(
                    dto.getLeadEmail(),
                    dto.getLeadName(),
                    agreementPdf
            );
            
            leadService.changeStatus(lead.getId(), dto.getCompanyId(), LeadStatus.AGREEMENT_SENT);

            return "Agreement sent successfully";

        } catch (JsonProcessingException e) {

            throw new RuntimeException(
                    "Failed to convert agreement dto to json",
                    e
            );
        }
    }
    
    @Override
    public String sendAgreementCustomOnMail(
            String name,
            String email,
            MultipartFile pdf
    ) {

        try {

        	mailService.sendAgreementEmail(
                    email,
                    name,
                    pdf.getBytes()
            );

            return "Agreement sent successfully";

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to read pdf",
                    e
            );
        }
    }

	@Override
	public Contract cancelContract(ContractDeleteDto dto) {
		
		Contract contract = contractRepo.findById(dto.getContractId()).orElse(null);
		
		if(contract==null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contract not found with id : "+dto.getContractId());
		}
		
		contract.setActualEndDate(dto.getActualEndDate());
		
		contract.setCancelContractType(dto.getCancelContractType());		
		
		contract.setCancelDescription(dto.getCancelDescription());
				
		if(dto.getNoticePeriodStartDate()!=null) {
			contract.setNoticePeriodStartDate(dto.getNoticePeriodStartDate());
		}
		
		Contract response = contractRepo.save(contract);
		
		return response;
		
	}
	
	@Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void terminateExpiredContracts() {

        LocalDate today = LocalDate.now();

        List<Contract> contracts =
                contractRepo.findContractsToTerminate(today);
 
        log.info(
                "Found {} contracts to terminate",
                contracts.size()
        );

        for (Contract contract : contracts) {

            try {

                List<ContractSeatMapping> mappings =
                        seatMappingRepo.findByContractIdAndCompanyId(
                                contract.getId(),
                                contract.getCompanyId()
                        );

                for (ContractSeatMapping mapping : mappings) {
                    mapping.setDeleted(true);
                }

                seatMappingRepo.saveAll(mappings);

                contract.setActive(false);

                contract.setContractStatus(
                        ContractStatus.TERMINATED
                );

                contractRepo.save(contract);

                log.info(
                        "Contract {} terminated successfully",
                        contract.getId()
                );

            } catch (Exception ex) {

                log.error(
                        "Failed to terminate contract {}",
                        contract.getId(),
                        ex
                );
            }
        }
	}
    
}
