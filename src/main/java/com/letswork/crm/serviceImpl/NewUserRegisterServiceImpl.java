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

        NewUserRegister existing =
                repo.findByEmailAndCompanyId(user.getEmail(), user.getCompanyId());

        if (existing != null) {
            throw new RuntimeException("User already registered with this email");
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
                repo.findByEmailAndCompanyId(email, companyId);
        
        if(user==null) {
        	throw new RuntimeException("User not found");
        }
                        

        File tempFile;
        try {
            tempFile = File.createTempFile("profile-", imageFile.getOriginalFilename());
            imageFile.transferTo(tempFile);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process image", e);
        }

        String imageUrl =
                s3Service.uploadUserProfileImage(
                        "letsworkcentres",
                        companyId,
                        email,
                        imageFile.getOriginalFilename(),
                        tempFile
                );

        user.setProfileImagePath(imageUrl);
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
            String companyId
    ) {

        NewUserRegister user =
                repo.findByEmailAndCompanyId(
                        email,
                        companyId
                );

        if (user == null) {
            throw new RuntimeException(
                    "User not found for email: " + email
            );
        }

        return user;
    }
}
