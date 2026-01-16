package com.letswork.crm.serviceImpl;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.NewUserRegisterRepository;
import com.letswork.crm.service.NewUserRegisterService;
import com.letswork.crm.service.TenantService;

@Service
@Transactional
public class NewUserRegisterServiceImpl
        implements NewUserRegisterService {

    @Autowired
    private NewUserRegisterRepository repo;

    @Autowired
    private TenantService tenantService;
    
    @Autowired
    private S3Service s3Service;
    

    ModelMapper mapper = new ModelMapper();

    @Override
    public NewUserRegister save(NewUserRegister user) {

        Tenant tenant =
                tenantService.findTenantByCompanyId(user.getCompanyId());

        if (tenant == null) {
            throw new RuntimeException("Invalid companyId: " + user.getCompanyId());
        }

        if (repo.findByEmailAndCompanyId(
                user.getEmail(), user.getCompanyId()).isPresent()) {

            throw new RuntimeException("User already registered with this email");
        }

        if (repo.findByPhoneNumberAndCompanyId(
                user.getPhoneNumber(), user.getCompanyId()).isPresent()) {

            throw new RuntimeException("User already registered with this phone number");
        }

        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());

        return repo.save(user);
    }
    
    @Override
    public NewUserRegister updateProfileImage(
            String companyId,
            String email,
            MultipartFile imageFile) {

        NewUserRegister user =
                repo.findByEmailAndCompanyId(email, companyId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "User not found for email: " + email));

        File tempFile;
        try {
            tempFile = File.createTempFile(
                    "profile-",
                    imageFile.getOriginalFilename()
            );
            imageFile.transferTo(tempFile);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to process image", e);
        }

        String imagePath =
                s3Service.uploadUserProfileImage(
                        "letsworkcentres",
                        companyId,
                        email,
                        imageFile.getOriginalFilename(),
                        tempFile
                );

        user.setProfileImagePath(imagePath); // store KEY or PATH
        user.setUpdateDate(new Date());

        return repo.save(user);
    }


    @Override
    public List<NewUserRegister> getAllByCompanyId(
            String companyId
    ) {
        return repo.findByCompanyId(companyId);
    }

    @Override
    public NewUserRegister getByEmailAndCompanyId(
            String email,
            String companyId) {

        return repo.findByEmailAndCompanyId(email, companyId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found for email: " + email));
    }

	@Override
	public void updateDayPass(String numberOfDays, String email, String companyId) {
		// TODO Auto-generated method stub
		
		if (numberOfDays == null) {
            return; 
        }

        int daysToAdd = Integer.parseInt(numberOfDays);

        NewUserRegister user =
                repo.findByEmailAndCompanyId(email, companyId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found for email: " + email
                                )
                        );

        Integer existingDays =
                user.getDayPass() == null ? 0 : user.getDayPass();

        user.setDayPass(existingDays + daysToAdd);
        user.setUpdateDate(new Date());

        repo.save(user);
    
		
		
	}
	
	
	@Override
	public void updateConferenceCredits(
	        String numberOfHours,
	        String email,
	        String companyId
	) {

	    if (numberOfHours == null) {
	        return;
	    }

	    int hours = Integer.parseInt(numberOfHours);

	    int creditsToAdd = hours * 2;

	    NewUserRegister user =
	            repo.findByEmailAndCompanyId(email, companyId)
	                    .orElseThrow(() ->
	                            new RuntimeException(
	                                    "User not found for email: " + email
	                            )
	                    );

	    Integer existingCredits =
	            user.getConferenceCredits() == null
	                    ? 0
	                    : user.getConferenceCredits();

	    user.setConferenceCredits(
	            existingCredits + creditsToAdd
	    );

	    user.setUpdateDate(new Date());
	    repo.save(user);
	}

	@Override
	public NewUserRegister setUserMonthly(String email, String companyId) {
		// TODO Auto-generated method stub
		NewUserRegister user =
                repo.findByEmailAndCompanyId(email, companyId)
                        .orElseThrow(() ->
                                new RuntimeException("User not found")
                        );

        user.setMonthly(true);
        return repo.save(user);
	}

	@Override
	public String resetMonthlyBenefits(String companyId) {
		// TODO Auto-generated method stub
		List<NewUserRegister> monthlyUsers =
                repo.findByMonthlyTrueAndCompanyId(companyId);

        if (monthlyUsers.isEmpty()) {
            return "No monthly users found";
        }

        for (NewUserRegister user : monthlyUsers) {
            user.setFreeConferenceCredits(4);
            user.setFreeDayPass(5);
        }

        repo.saveAll(monthlyUsers);

        return "Monthly benefits reset successfully";
    }
	
	
}
