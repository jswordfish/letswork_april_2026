package com.letswork.crm.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.NewUserRegister;

public interface NewUserRegisterService {
	
	public NewUserRegister save(NewUserRegister user);

    List<NewUserRegister> getAllByCompanyId(String companyId);

    NewUserRegister getByEmailAndCompanyId(
            String email,
            String companyId);
    
    public NewUserRegister updateProfileImage(
            String companyId,
            String email,
            MultipartFile imageFile);
    
    void updateDayPass(String numberOfDays, String email, String companyId);
    
    void updateConferenceCredits(
            String numberOfHours,
            String email,
            String companyId
    );
    
    public NewUserRegister setUserMonthly(
            String email,
            String companyId
    );
    
    public String resetMonthlyBenefits(String companyId);
    
    List<String> getAllCategories(String companyId);

    List<String> getSubCategories(
            String companyId,
            String category
    );

    List<NewUserRegister> getUsersBySubCategory(
            String companyId,
            String category,
            String subCategory
    );
    
    NewUserRegister saveOrUpdateManually(NewUserRegister user);
    
    public PaginatedResponseDto getPaginated(
            String companyId,
            String email,
            String letsWorkCentre,
            String city,
            String state,
            String category,
            String subCategory,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size
    );

}
