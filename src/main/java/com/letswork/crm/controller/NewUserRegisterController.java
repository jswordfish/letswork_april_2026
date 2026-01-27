package com.letswork.crm.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.repo.NewUserRegisterRepository;
import com.letswork.crm.service.NewUserRegisterService;
import com.letswork.crm.serviceImpl.OtpService;
import com.letswork.crm.util.TokenService2;

@RestController
@CrossOrigin
@RequestMapping("/letsWork-clients")
public class NewUserRegisterController {

    @Autowired
    private NewUserRegisterService service;
    
    @Autowired
    private OtpService otpService;
    
    @Autowired
    NewUserRegisterRepository newUserRegisterRepository;
    
    TokenService2 tokenService = new TokenService2();

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestBody NewUserRegister user) {

        NewUserRegister saved = service.save(user);

        String token =
                tokenService.generateToken("App User", saved.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", saved);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/admin/save-or-update")
    public ResponseEntity<NewUserRegister> saveOrUpdateManually(
            @RequestBody NewUserRegister user,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(
                service.saveOrUpdateManually(user)
        );
    }
    
    @GetMapping
    public ResponseEntity<PaginatedResponseDto> get(
            @RequestParam String companyId,
            @RequestParam String token,

            @RequestParam(required = false) String email,
            @RequestParam(required = false) String letsWorkCentre,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,

            @RequestParam(required = false) String category,
            @RequestParam(required = false) String subCategory,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fromDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate toDate,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                service.getPaginated(
                        companyId,
                        email,
                        letsWorkCentre,
                        city,
                        state,
                        category,
                        subCategory,
                        fromDate,
                        toDate,
                        page,
                        size
                )
        );
    }
    
    @PostMapping("/set-monthly")
    public ResponseEntity<NewUserRegister> setMonthly(
            @RequestParam String email,
            @RequestParam String companyId,
            @RequestParam String token
    ) {
    	
    	
        return ResponseEntity.ok(
                service.setUserMonthly(email, companyId)
        );
    }
    
    @PostMapping("/reset-monthly-benefits")
    public ResponseEntity<String> resetMonthlyBenefits(
            @RequestParam String companyId
    ) {
    	
    	LocalDate today = LocalDate.now();
        String dateString = today.toString();
    	
    	otpService.sendResetCreditsEmail("Dhruvjani4321@gmail.com", dateString);
    	
        return ResponseEntity.ok(
                service.resetMonthlyBenefits(companyId)
        );
    }
    
    @GetMapping("/checkRegister")
    public ResponseEntity<Map<String, String>> checkRegister(
            @RequestParam String email,
            @RequestParam String number,
            @RequestParam String companyId) {

        Map<String, String> response = new HashMap<>();

        Optional<NewUserRegister> emailUser =
        		newUserRegisterRepository
                        .findByEmailAndCompanyId(email, companyId);

        if (emailUser.isPresent()) {
            response.put("status", "EMAIL_ALREADY_REGISTERED");
            return ResponseEntity.ok(response);
        }

        Optional<NewUserRegister> phoneUser =
        		newUserRegisterRepository
                        .findByPhoneNumberAndCompanyId(number, companyId);

        if (phoneUser.isPresent()) {
            response.put("status", "PHONE_ALREADY_REGISTERED");
            return ResponseEntity.ok(response);
        }

        response.put("status", "NEW_USER");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping(
	        value = "/profile-image",
	        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
    public ResponseEntity<Map<String, Object>> uploadProfileImage(
            @RequestParam String companyId,
            @RequestParam String email,
            @RequestPart MultipartFile image,
            @RequestParam String token) {

        NewUserRegister updatedUser = service.updateProfileImage(companyId, email, image);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Profile image updated successfully");
        response.put("user", updatedUser);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/NewUsers-get")
    public ResponseEntity<?> getUsers(
            @RequestParam String companyId,
            @RequestParam(required = false) String email
            ) {

        if (email != null && !email.isEmpty()) {

            return ResponseEntity.ok(service.getByEmailAndCompanyId(email,companyId));
        }

        return ResponseEntity.ok(
                service.getAllByCompanyId(companyId)
        );
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories(
            @RequestParam String companyId,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(
        		service.getAllCategories(companyId)
        );
    }

    @GetMapping("/sub-categories")
    public ResponseEntity<List<String>> getSubCategories(
            @RequestParam String companyId,
            @RequestParam String category,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(
        		service.getSubCategories(companyId, category)
        );
    }

    @GetMapping("/by-sub-category")
    public ResponseEntity<List<NewUserRegister>> getUsersBySubCategory(
            @RequestParam String companyId,
            @RequestParam String category,
            @RequestParam String subCategory,
            @RequestParam String token
    ) {
        return ResponseEntity.ok(
        		service.getUsersBySubCategory(
                        companyId,
                        category,
                        subCategory
                )
        );
    }
}
