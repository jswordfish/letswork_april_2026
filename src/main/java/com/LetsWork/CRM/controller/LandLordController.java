package com.LetsWork.CRM.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.LandLord;
import com.LetsWork.CRM.service.LandLordService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@CrossOrigin
public class LandLordController {
	
	
	private final LandLordService landLordService;
    private final ObjectMapper objectMapper;

    @Autowired
    public LandLordController(LandLordService landLordService, ObjectMapper objectMapper) {
        this.landLordService = landLordService;
        this.objectMapper = objectMapper;
    }
    
    @PostMapping(
    	    path = "/create-or-update-land-lord",
    	    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    	)
    public ResponseEntity<?> saveOrUpdate(
            @RequestPart("landLord") String landLordJson,
            @RequestPart(value = "panFile", required = false) MultipartFile panFile,
            @RequestPart(value = "aadharFile", required = false) MultipartFile aadharFile,
            @RequestPart(value = "gstFile", required = false) MultipartFile gstFile,
            @RequestPart(value = "agreementFile", required = false) MultipartFile agreementFile,
            @RequestParam String token
    ) throws Exception {

        LandLord landLord = objectMapper.readValue(landLordJson, LandLord.class);
        LandLord saved = landLordService.saveOrUpdate(landLord, panFile, aadharFile, gstFile, agreementFile);
        return ResponseEntity.ok(saved);
    }
    
    @DeleteMapping("/delete-by-id")
    public ResponseEntity<?> delete(@RequestParam Long id, @RequestParam String token) {
        landLordService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/find-by-gst-number")
    public ResponseEntity<?> findByGst(@RequestParam String gstNumber, @RequestParam String token) {
        return landLordService.findByGstNumber(gstNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/get-all")
    public ResponseEntity<PaginatedResponseDto> listAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "companyId", required = false) String companyId,
            @RequestParam String token
    ) {
        PaginatedResponseDto dto = landLordService.listAll(page, companyId);
        return ResponseEntity.ok(dto);
    }
	
	
}
