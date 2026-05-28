package com.letswork.crm.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Category;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.entities.SubCategory;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.enums.CategoryType;
import com.letswork.crm.repo.CategoryRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.LetsWorkClientRepository;
import com.letswork.crm.repo.NewUserRegisterRepository;
import com.letswork.crm.repo.ReferralRepository;
import com.letswork.crm.repo.SubCategoryRepository;
import com.letswork.crm.service.LetsWorkCentreService;
import com.letswork.crm.service.NewUserRegisterService;
import com.letswork.crm.service.TenantService;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;

@Service
@Transactional
public class NewUserRegisterServiceImpl
        implements NewUserRegisterService {

    @Autowired
    private NewUserRegisterRepository repo;

    @Autowired
    private TenantService tenantService;
    
    @Autowired
    ReferralRepository referralRepository;
    
    @Autowired
    LetsWorkCentreService letsWorkCentreService;
    
    @Autowired
    private S3Service s3Service;
    
    @Autowired
	LetsWorkCentreRepository letsWorkCentreRepo;
    
    @Autowired
    CategoryRepository categoryRepo;
    
    @Autowired
    SubCategoryRepository subCategoryRepo;
    
    @Autowired
    LetsWorkClientRepository letsWorkClientRepo;
    

    ModelMapper mapper = new ModelMapper();

    @Override
    public NewUserRegister save(NewUserRegister user) {

        Tenant tenant =
                tenantService.findTenantByCompanyId(user.getCompanyId());

        if (tenant == null) {
            throw new RuntimeException("Invalid companyId: " + user.getCompanyId());
        }

        if (repo.findByEmailAndCompanyId(
                user.getEmail(),
                user.getCompanyId()).isPresent()) {

            throw new RuntimeException("User already registered with this email");
        }

        if (repo.findByPhoneNumberAndCompanyId(
                user.getPhoneNumber(),
                user.getCompanyId()).isPresent()) {

            throw new RuntimeException("User already registered with this phone number");
        }

        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setActive(true);

        user.setInternal(false);

        NewUserRegister saved = repo.save(user);
        
        String clientCompanyName = user.getClientCompanyName()+"_"+user.getEmail();

        // 🔥 Pass the transient value directly from the original incoming 'user' object
        createClientCompanyMapping(saved, clientCompanyName);

        return saved;
    }
    
    @Override
    public NewUserRegister saveOrUpdateManually(NewUserRegister user) {

        Tenant tenant =
                tenantService.findTenantByCompanyId(user.getCompanyId());

        if (tenant == null) {
            throw new RuntimeException("Invalid companyId - " + user.getCompanyId());
        }

        LetsWorkCentre centre =
                letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(
                        user.getLetsWorkCentre(),
                        user.getCompanyId(),
                        user.getCity(),
                        user.getState()
                );

        if (centre == null) {
            throw new RuntimeException("This LetsWorkCentre does not exists");
        }

        if (user.getCategory() != null) {

            Category category =
                    categoryRepo.findByNameAndCompanyIdAndCategoryType(
                            user.getCategory(),
                            user.getCompanyId(),
                            CategoryType.BUSINESS
                    );

            if (category == null) {
                throw new RuntimeException("Invalid category");
            }
        }

        if (user.getSubCategory() != null) {

            SubCategory sub =
                    subCategoryRepo.findByNameAndCompanyIdAndCategoryType(
                            user.getSubCategory(),
                            user.getCompanyId(),
                            CategoryType.BUSINESS
                    );

            if (sub == null) {
                throw new RuntimeException("Invalid sub-category");
            }
        }

        if (Boolean.TRUE.equals(user.getInternal())) {

            NewUserRegister existing = null;

            if (user.getEmail() != null) {
                existing = repo.findByEmailAndCompanyId(
                        user.getEmail(),
                        user.getCompanyId()
                ).orElse(null);
            }

            if (existing == null && user.getPhoneNumber() != null) {
                existing = repo.findByPhoneNumberAndCompanyId(
                        user.getPhoneNumber(),
                        user.getCompanyId()
                ).orElse(null);
            }

            if (existing != null) {

                existing.setName(user.getName());
                existing.setDob(user.getDob());
                existing.setProfileImagePath(user.getProfileImagePath());

                existing.setConferenceCredits(user.getConferenceCredits());
                existing.setDayPass(user.getDayPass());
                existing.setFreeConferenceCredits(user.getFreeConferenceCredits());
                existing.setFreeDayPass(user.getFreeDayPass());
                existing.setMonthly(user.getMonthly());

                existing.setUpdateDate(new Date());

                NewUserRegister saved = repo.save(existing);

                // 🔥 Extract the transient field from the original DTO, not the saved entity
                createClientCompanyMapping(saved, user.getClientCompanyName());

                return saved;
            }

            user.setCreateDate(new Date());
            user.setUpdateDate(new Date());
            user.setActive(true);
            user.setInternal(true);

            NewUserRegister saved = repo.save(user);

            // 🔥 Extract the transient field from the original DTO, not the saved entity
            createClientCompanyMapping(saved, user.getClientCompanyName());

            return saved;
        }
        
        return null;
    }
    
    private void createClientCompanyMapping(NewUserRegister user, String excelCompanyName) {

        if (excelCompanyName == null || excelCompanyName.trim().isEmpty()) {
            throw new RuntimeException("ClientCompanyName missing in Excel for user: " + user.getEmail());
        }

        excelCompanyName = excelCompanyName.trim();

        boolean exists = letsWorkClientRepo
                .existsByUserIdAndClientCompanyNameAndCompanyId(
                        user.getId(),
                        excelCompanyName,
                        user.getCompanyId()
                );

        if (exists) {
            return;
        }

        LetsWorkClient client = new LetsWorkClient();

        // 🔥 DO NOT modify Excel value
        client.setClientCompanyName(excelCompanyName);

        client.setEmail(user.getEmail());
        client.setPhone(user.getPhoneNumber());

        client.setCategory(user.getCategory());
        client.setSubCategory(user.getSubCategory());

        client.setLetsWorkCentre(user.getLetsWorkCentre());
        client.setCity(user.getCity());
        client.setState(user.getState());

        client.setCompanyId(user.getCompanyId());
        client.setUserId(user.getId());

        client.setCreateDate(new Date());
        client.setUpdateDate(new Date());
        
        client.getUsers().add(user);

        letsWorkClientRepo.save(client);
    }
    
    private void createClientCompanyIfNotExists(NewUserRegister user) {

        boolean exists =
                letsWorkClientRepo
                    .findByClientCompanyNameAndCompanyId(
                            user.getClientCompanyName(),
                            user.getCompanyId()
                    )
                    .isPresent();

        if (exists) return;

        LetsWorkClient client = new LetsWorkClient();

        client.setClientCompanyName(user.getClientCompanyName() != null ? user.getClientCompanyName() : user.getName());
        client.setEmail(user.getEmail());
        client.setPhone(user.getPhoneNumber());
        client.setCategory(user.getCategory());
        client.setSubCategory(user.getSubCategory());
        client.setLetsWorkCentre(user.getLetsWorkCentre());
        client.setCity(user.getCity());
        client.setState(user.getState());
        client.setCompanyId(user.getCompanyId());
        client.setUserId(user.getId());

        client.setCreateDate(new Date());
        client.setUpdateDate(new Date());

        letsWorkClientRepo.save(client);
    }
    
    private void updateClientCompanyNameIfChanged(NewUserRegister user, String oldCompanyName) {

        String newCompanyName = user.getClientCompanyName();

//        if (newCompanyName == null || newCompanyName.equals(oldCompanyName)) {
//            return;
//        }

        List<LetsWorkClient> clients = letsWorkClientRepo
                .findByUserIdAndCompanyId(user.getId(), user.getCompanyId());

        if (clients == null || clients.isEmpty()) return;

        for (LetsWorkClient client : clients) {
            client.setClientCompanyName(newCompanyName);
            client.setUpdateDate(new Date());
        }

        letsWorkClientRepo.saveAll(clients);
    }
    
    @Override
    public void disableUser(NewUserRegister user) {
    	
    	if (Boolean.FALSE.equals(user.getActive())) {
    		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user's account is already deactivated");
    	}
    	
    	user.setActive(false);
    	
    	repo.save(user);
    	
    	
    }
    
    @Override
    public String uploadNewUserFromExcel(
            MultipartFile file,
            String companyId
    ) throws IOException {

        List<NewUserRegister> userRegisters =
                Poiji.fromExcel(
                        file.getInputStream(),
                        PoijiExcelType.XLSX,
                        NewUserRegister.class
                );

        
        for (NewUserRegister dto : userRegisters) {
            String val = validate(dto);
            if (!val.equalsIgnoreCase("ok")) {
                return "Validation failed: " + val;
            }
        }

        List<String> errors = new ArrayList<>();

        
        for (NewUserRegister newUserRegister : userRegisters) {

            try {

                newUserRegister.setCompanyId(companyId);
                newUserRegister.setInternal(true);

                saveOrUpdateManually(newUserRegister);

            } catch (Exception e) {

                errors.add(
                        "Error saving "
                                + newUserRegister.getEmail()
                                + ": "
                                + e.getMessage()
                );
            }
        }

        

        if (!errors.isEmpty()) {

            return "UPLOAD PARTIALLY FAILED:\n" + String.join("\n", errors);
        }

        return "ok";
    }
    
    private String validate(NewUserRegister dto) {
		if(dto.getName() == null || dto.getName().length() == 0) {
			return "Name Should not be null";
		}
	 			
//		if(dto.getCompanyId() == null || dto.getCompanyId().length() == 0) {
//			return "CompanyId Should not be null";	
//			}
		
		if(dto.getEmail() == null || dto.getEmail().length() == 0) {
			return "Email Should not be null";	
			}
		
		if(dto.getPhoneNumber() == null || dto.getPhoneNumber().length() == 0) {
			return "Phone Number Should not be null";	
			}
		
//		if(dto.getDob() == null) {
//			return "Date of Birth Should not be null";	
//			}
		
		
//		if(dto.getCategory() == null || dto.getCategory().length() == 0) {
//			return "Category Should not be null";	
//			}
//		
//		if(dto.getSubCategory() == null || dto.getSubCategory().length() == 0) {
//			return "SubCategory Should not be null";	
//			}
		
		if(dto.getLetsWorkCentre() == null || dto.getLetsWorkCentre().length() == 0) {
			return "LetsWorkCentre Should not be null";	
			}
		
		if(dto.getCity() == null || dto.getCity().length() == 0) {
			return "City Should not be null";	
			}
		
		if(dto.getState() == null || dto.getState().length() == 0) {
			return "State Should not be null";	
			}
		
		
//		if(tenantService.findTenantByCompanyId(dto.getCompanyId())==null) {
//			return "CompanyId "+dto.getCompanyId()+" does not exists";
//		}
		
//		if(letsWorkCentreService.findByName(dto.getLetsWorkCentre(), dto.getCompanyId(), dto.getCity(), dto.getState()) == null){
//			return "Letswork Cente "+dto.getLetsWorkCentre()+" does not exist";
//		}
//		
//		if(categoryRepo.findByNameAndCompanyIdAndCategoryType(dto.getCategory(), dto.getCompanyId(), CategoryType.BUSINESS)==null) {
//			return "Category "+dto.getCategory()+" does not exists";
//		}
//		
//		if(subCategoryRepo.findByNameAndCompanyIdAndCategoryType(dto.getSubCategory(), dto.getCompanyId(), CategoryType.BUSINESS)==null) {
//			return "Sub-Category "+dto.getSubCategory()+" does not exists";
//		}
		
		 		
		return "ok";
	}
    
    @Override
    public PaginatedResponseDto getPaginated(
            String companyId,
            String email,
            String letsWorkCentre,
            String city,
            String state,
            String category,
            String subCategory,
            String search,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size
    ) {
        Pageable pageable =
                PageRequest.of(page, size, Sort.by("createDate").descending());

        Page<NewUserRegister> resultPage =
                repo.filter(
                        companyId,
                        email,
                        letsWorkCentre,
                        city,
                        state,
                        category,
                        subCategory,
                        search,
                        fromDate == null ? null : java.sql.Date.valueOf(fromDate),
                        toDate == null ? null : java.sql.Date.valueOf(toDate),
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
        user.setUpdateDate(new Date());
        NewUserRegister updatedUser = repo.save(user);

        
        referralRepository.findByEmailAndCompanyId(email, companyId)
                .ifPresent(referral -> {
                    referral.setJoiningDate(LocalDate.now());
                    referral.setUpdateDate(new Date());
                    referralRepository.save(referral);
                });

        return updatedUser;
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
	
	private void validateCompany(String companyId) {
        if (tenantService.findTenantByCompanyId(companyId) == null) {
            throw new RuntimeException("Invalid companyId - " + companyId);
        }
    }

    @Override
    public List<String> getAllCategories(String companyId) {
        validateCompany(companyId);
        return repo.findDistinctCategories(companyId);	
    }

    @Override
    public List<String> getSubCategories(
            String companyId,
            String category
    ) {
        validateCompany(companyId);
        return repo.findDistinctSubCategories(companyId, category);
    }

    @Override
    public List<NewUserRegister> getUsersBySubCategory(
            String companyId,
            String category,
            String subCategory
    ) {
        validateCompany(companyId);
        return repo.findByCompanyIdAndCategoryAndSubCategory(
                companyId,
                category,
                subCategory
        );
    }
	
	
}
