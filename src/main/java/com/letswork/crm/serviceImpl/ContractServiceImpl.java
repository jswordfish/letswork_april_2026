package com.letswork.crm.serviceImpl;

import java.io.IOException;
import java.util.Date;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letswork.crm.dtos.AgreementDto;
import com.letswork.crm.dtos.ConvertedContractDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Contract;
import com.letswork.crm.entities.Lead;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.ContractStatus;
import com.letswork.crm.enums.LeadStatus;
import com.letswork.crm.repo.ContractRepository;
import com.letswork.crm.repo.LeadRepo;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.service.ContractService;
import com.letswork.crm.service.LeadService;
import com.letswork.crm.service.NewUserRegisterService;
import com.letswork.crm.service.TenantService;

@Service
@Transactional
public class ContractServiceImpl implements ContractService {

    @Autowired
    private ContractRepository contractRepo;

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
    public ConvertedContractDto saveOrUpdateConverted(ConvertedContractDto dto) {

        Tenant tenant = tenantService.findTenantByCompanyId(dto.getContract().getCompanyId());
        if (tenant == null) {
            throw new RuntimeException("Invalid companyId");
        }

        LetsWorkClient client = letsWorkClientRepo
                .findByIdAndCompanyId(dto.getContract().getLetsWorkClient().getId(), dto.getContract().getCompanyId())
                .orElseThrow(() -> new RuntimeException("Invalid LetsWorkClient"));

        dto.getContract().setLetsWorkClient(client);

        if (dto.getContract().getId() != null) {

            Contract existing = contractRepo
                    .findByIdAndCompanyId(dto.getContract().getId(), dto.getContract().getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Contract not found"));

            dto.getContract().setCreateDate(existing.getCreateDate());
            dto.getContract().setUpdateDate(new Date());

            mapper.map(dto.getContract(), existing);
            Contract saved = contractRepo.save(existing);

            byte[] pdfBytes = contractDocumentService.generateAgreementPdf(saved);

            String s3Key = s3Service.uploadContractAgreementPdf(
            		"letsworkcentres",
                    saved.getCompanyId(),
                    saved.getId(),
                    pdfBytes
            );

            saved.setAgreementS3KeyName(s3Key);
            
			ConvertedContractDto response = new ConvertedContractDto();
            
            if(dto.getNewUserRegister()!=null) {

			NewUserRegister user = newUserRegisterService.saveOrUpdateManually(dto.getNewUserRegister());
			response.setNewUserRegister(user);
            }
            else response.setNewUserRegister(null);
            
            Contract contract = contractRepo.save(saved);
            
            response.setContract(contract);
            
            return response;
        }

        else {
        	dto.getContract().setCreateDate(new Date());

            Contract saved = contractRepo.save(dto.getContract());

            byte[] pdfBytes = contractDocumentService.generateAgreementPdf(saved);

            String s3Key = s3Service.uploadContractAgreementPdf(
            		"letsworkcentres",
                    saved.getCompanyId(),
                    saved.getId(),
                    pdfBytes
            );

            saved.setAgreementS3KeyName(s3Key);
            
            ConvertedContractDto response = new ConvertedContractDto();
            
            if(dto.getNewUserRegister()!=null) {

			NewUserRegister user = newUserRegisterService.saveOrUpdateManually(dto.getNewUserRegister());
			response.setNewUserRegister(user);
            }
            else response.setNewUserRegister(null);
            
            Contract contract = contractRepo.save(saved);
            
            response.setContract(contract);
            
            return response;
        }
    }
    

    @Override
    public PaginatedResponseDto getPaginated(
            String companyId,
            Long letsWorkClientId,
            ContractStatus status,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<Contract> resultPage = contractRepo.filter(
                companyId,
                letsWorkClientId,
                status,
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
    public String sendAgreementDefaultOnMail(AgreementDto dto) {

        try {

            Lead lead =
                    leadRepo.findByEmailAndCompanyId(
                            dto.getLeadEmail(),
                            dto.getCompanyId()
                    );

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
    
}
