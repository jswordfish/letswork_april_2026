package com.letswork.crm.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letswork.crm.dtos.AgreementDto;
import com.letswork.crm.dtos.BulkSeatAssignmentRequestContract;
import com.letswork.crm.dtos.ContractDeleteDto;
import com.letswork.crm.dtos.ConvertedContractDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Contract;
import com.letswork.crm.enums.ContractStatus;
import com.letswork.crm.enums.DateFilterType;
import com.letswork.crm.enums.LeadStatus;
import com.letswork.crm.repo.ContractRepository;
import com.letswork.crm.service.ContractSeatMappingService;
import com.letswork.crm.service.ContractService;
import com.letswork.crm.service.LeadService;

@RestController
@RequestMapping("/contracts")
public class ContractController {

    @Autowired
    private ContractService contractService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ContractRepository contractRepo;
    
    @Autowired
    private ContractSeatMappingService mappingService;
    
    @Autowired
    LeadService leadService;

    @PostMapping
    public ResponseEntity<Contract> saveOrUpdate(
            @RequestBody Contract contract,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(contractService.saveOrUpdate(contract));
    }
    
    @PostMapping(
            value = "/lead-contract",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ConvertedContractDto> saveOrUpdateLead(
            @RequestPart("data") String data,
            @RequestPart("assign") String assign,
            @RequestPart(value = "agreement", required = false)
            MultipartFile agreement,
            @RequestParam(required = false) Long leadId,
            @RequestParam String companyId,
            @RequestParam String token
    ) throws Exception {

        ConvertedContractDto dto =
                objectMapper.readValue(data, ConvertedContractDto.class);

        BulkSeatAssignmentRequestContract assignment =
                objectMapper.readValue(assign, BulkSeatAssignmentRequestContract.class);

        ConvertedContractDto response = contractService.saveOrUpdateConverted(
                dto,
                agreement,
                assignment
        );

        if ((leadId != null) && (leadId != -1)) {
            leadService.changeStatus(leadId, companyId, LeadStatus.CONVERTED);
        }

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/onboarding-link")
    public ResponseEntity<String> getOnboardingLink(
    		@RequestParam String token,
    		@RequestParam Long contractId,
    		@RequestParam String companyId)
    {
    	
    	String baseUrl = "https://letsworkadmin.vercel.app/onboarding?letsWorkClientId="; //test
    	
//    	String baseUrl = "https://letsworkapp.in/onboarding?letsWorkClientId="; //live
    	
    	Contract contract = contractRepo.findByIdAndCompanyId(contractId, companyId).orElse(null);
    	
    	if(contract==null) {
    		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contract not found");
    	}
    	
    	Long letsWorkClientId = contract.getLetsWorkClient().getId();
    	
    	return ResponseEntity.ok(baseUrl+letsWorkClientId);
    	
    }
    
    @PostMapping("/send-agreement-default")
    public ResponseEntity<String> cancelContract(
            @RequestBody AgreementDto dto,
            @RequestParam String token
    ) {

        return ResponseEntity.ok(
                contractService.sendAgreementDefaultOnMail(dto)
        );
    }
    
    @PostMapping(
            value = "/send-agreement-custom",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> sendAgreementCustom(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam MultipartFile pdf,
            @RequestParam String token
    ) {

        return ResponseEntity.ok(
        		contractService.sendAgreementCustomOnMail(
                        name,
                        email,
                        pdf
                )
        );
    }
    
    @PostMapping("/cancel")
    public ResponseEntity<Contract> sendAgreementDefault(
    		@RequestBody ContractDeleteDto dto,
            @RequestParam String token
    ) {

        return ResponseEntity.ok(
                contractService.cancelContract(dto)
        );
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto> getPaginated(
            @RequestParam String companyId,
            @RequestParam String token,

            @RequestParam(required = false) Long letsWorkClientId,
            @RequestParam(required = false) ContractStatus contractStatus,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @RequestParam(required = false)
            DateFilterType dateFilterType,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                contractService.getPaginated(
                        companyId,
                        letsWorkClientId,
                        contractStatus,
                        fromDate,
                        toDate,
                        dateFilterType,
                        page,
                        size
                )
        );
    }
    
}
