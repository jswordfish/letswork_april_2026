package com.letswork.crm.controller;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Greviance;
import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.enums.GrevianceStatus;
import com.letswork.crm.repo.NewUserRegisterRepository;
import com.letswork.crm.service.GrevianceService;
import com.letswork.crm.serviceImpl.MailJetOtpService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/greviance")
@RequiredArgsConstructor
public class GrevianceController {

    private final GrevianceService grevianceService;
    private final NewUserRegisterRepository userRepo;
    
    @Autowired
    MailJetOtpService mailService;

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Greviance> save(
            @RequestParam String token,
            @RequestPart("greviance") String grevianceJson,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {

        Greviance greviance =
                new ObjectMapper().readValue(grevianceJson, Greviance.class);

        NewUserRegister user = userRepo.findByEmailAndCompanyId(
                greviance.getEmail(),
                greviance.getCompanyId()
        ).orElseThrow(() ->
                new RuntimeException("User not found for given email")
        );

        Greviance saved = grevianceService.saveGreviance(greviance, image);

        mailService.sendGrevianceEmail(
                saved.getEmail(),
                user.getName(),
                LocalDateTime.now(),
                saved.getCategory(),
                saved.getSubCategory(),
                saved.getLetsWorkCentre(),
                saved.getIssue(),
                saved.getImageS3Key()
        );

        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto> get(
            @RequestParam String companyId,
            @RequestParam String token,

            @RequestParam(required = false) String email,
            @RequestParam(required = false) String centre,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,

            @RequestParam(required = false) String category,
            @RequestParam(required = false) String subCategory,

            @RequestParam(required = false) GrevianceStatus grevianceStatus,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                grevianceService.getGreviances(
                        companyId,
                        email,
                        centre,
                        city,
                        state,
                        category,
                        subCategory,
                        grevianceStatus,
                        page,
                        size
                )
        );
    }
    
    @PutMapping("/update-status")
    public ResponseEntity<Greviance> updateStatus(
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam Long grevianceId,
            @RequestParam GrevianceStatus status
    ) {

        return ResponseEntity.ok(
                grevianceService.updateGrevianceStatus(
                        grevianceId,
                        status,
                        companyId
                )
        );
    }
    
}
