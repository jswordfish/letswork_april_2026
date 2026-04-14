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
                user.getEmail(), user.getCompanyId()).isPresent()) {

            throw new RuntimeException("User already registered with this email");
        }

        if (repo.findByPhoneNumberAndCompanyId(
                user.getPhoneNumber(), user.getCompanyId()).isPresent()) {

            throw new RuntimeException("User already registered with this phone number");
        }

        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setActive(true);
        
        NewUserRegister saved = repo.save(user);
        
        createClientCompanyIfNotExists(saved);

        return saved;
    }
    
    @Override
    public NewUserRegister saveOrUpdateManually(NewUserRegister user) {

        // 1️⃣ Validate company 
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

        // 2️⃣ Validate category
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

        // 3️⃣ Validate sub-category
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

        // 4️⃣ Find existing user
        NewUserRegister existing = null;

        if (user.getId() != null) {
            existing = repo.findById(user.getId()).orElse(null);
        }

        if (existing == null && user.getEmail() != null) {
            existing =
                    repo.findByEmailAndCompanyId(
                            user.getEmail(),
                            user.getCompanyId()
                    ).orElse(null);
        }

        NewUserRegister saved; // ✅ important

        // 5️⃣ UPDATE
        if (existing != null) {

            final Long existingId = existing.getId();

            repo.findByEmailAndCompanyId(
                    user.getEmail(),
                    user.getCompanyId()
            ).ifPresent(u -> {
                if (!u.getId().equals(existingId)) {
                    throw new RuntimeException("Email already in use");
                }
            });

            repo.findByPhoneNumberAndCompanyId(
                    user.getPhoneNumber(),
                    user.getCompanyId()
            ).ifPresent(u -> {
                if (!u.getId().equals(existingId)) {
                    throw new RuntimeException("Phone number already in use");
                }
            });

            user.setId(existing.getId());
            user.setCreateDate(existing.getCreateDate());
            user.setUpdateDate(new Date());

            mapper.map(user, existing);
            saved = repo.save(existing);

        } else {

            // 6️⃣ CREATE
            if (repo.findByEmailAndCompanyId(
                    user.getEmail(),
                    user.getCompanyId()
            ).isPresent()) {
                throw new RuntimeException("Email already exists");
            }

            if (repo.findByPhoneNumberAndCompanyId(
                    user.getPhoneNumber(),
                    user.getCompanyId()
            ).isPresent()) {
                throw new RuntimeException("Phone number already exists");
            }

            user.setCreateDate(new Date());
            user.setUpdateDate(new Date());
            user.setActive(true);
            saved = repo.save(user);
        }

        createClientCompanyIfNotExists(saved);

        return saved;
    }
    
    private void createClientCompanyIfNotExists(NewUserRegister user) {

        boolean exists =
                letsWorkClientRepo
                    .findByEmailAndCompanyId(
                            user.getEmail(),
                            user.getCompanyId()
                    )
                    .isPresent();

        if (exists) return;

        LetsWorkClient client = new LetsWorkClient();

        client.setClientCompanyName(user.getName());
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
    
    @Override
    public void disableUser(NewUserRegister user) {
    	
    	if (Boolean.FALSE.equals(user.getActive())) {
    		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user's account is already deactivated");
    	}
    	
    	user.setActive(false);
    	
    	repo.save(user);
    	
    	
    }
    
    @Override
	public String uploadNewUserFromExcel(MultipartFile file, String companyId) throws IOException {
    	
	    List<NewUserRegister> userRegisters = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, NewUserRegister.class);
	    
	    for(NewUserRegister dto : userRegisters) {
    		String val = validate(dto);
    		if(!val.equalsIgnoreCase("ok")) {
    			return val;
    		}
    	}
	    
	    List<String> responses = new ArrayList<>();

	    for (NewUserRegister newUserRegister : userRegisters) {
	        try {
	        	newUserRegister.setCompanyId(companyId); 

	            
	            saveOrUpdateManually(newUserRegister);

	            responses.add("Saved or Updated: " + newUserRegister.getName() + " " + newUserRegister.getEmail());
	        } catch (Exception e) {
	            responses.add("Error saving " + newUserRegister.getEmail() + ": " + e.getMessage());
	        }
	    }

	    return "ok";
	}
    
    private String validate(NewUserRegister dto) {
		if(dto.getName() == null || dto.getName().length() == 0) {
			return "Name Should not be null";
		}
	 			
		if(dto.getCompanyId() == null || dto.getCompanyId().length() == 0) {
			return "CompanyId Should not be null";	
			}
		
		if(dto.getEmail() == null || dto.getEmail().length() == 0) {
			return "Email Should not be null";	
			}
		
		if(dto.getPhoneNumber() == null || dto.getPhoneNumber().length() == 0) {
			return "Phone Number Should not be null";	
			}
		
		if(dto.getDob() == null) {
			return "Date of Birth Should not be null";	
			}
		
		
		if(dto.getCategory() == null || dto.getCategory().length() == 0) {
			return "Category Should not be null";	
			}
		
		if(dto.getSubCategory() == null || dto.getSubCategory().length() == 0) {
			return "SubCategory Should not be null";	
			}
		
		if(dto.getLetsWorkCentre() == null || dto.getLetsWorkCentre().length() == 0) {
			return "LetsWorkCentre Should not be null";	
			}
		
		if(dto.getCity() == null || dto.getCity().length() == 0) {
			return "City Should not be null";	
			}
		
		if(dto.getState() == null || dto.getState().length() == 0) {
			return "State Should not be null";	
			}
		
		
		if(tenantService.findTenantByCompanyId(dto.getCompanyId())==null) {
			return "CompanyId "+dto.getCompanyId()+" does not exists";
		}
		
		if(letsWorkCentreService.findByName(dto.getLetsWorkCentre(), dto.getCompanyId(), dto.getCity(), dto.getState()) == null){
			return "Letswork Cente "+dto.getLetsWorkCentre()+" does not exist";
		}
		
		if(categoryRepo.findByNameAndCompanyIdAndCategoryType(dto.getCategory(), dto.getCompanyId(), CategoryType.BUSINESS)==null) {
			return "Category "+dto.getCategory()+" does not exists";
		}
		
		if(subCategoryRepo.findByNameAndCompanyIdAndCategoryType(dto.getSubCategory(), dto.getCompanyId(), CategoryType.BUSINESS)==null) {
			return "Sub-Category "+dto.getSubCategory()+" does not exists";
		}
		
		 		
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
