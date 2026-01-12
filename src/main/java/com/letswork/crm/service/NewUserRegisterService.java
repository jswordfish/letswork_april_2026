package com.letswork.crm.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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

}
