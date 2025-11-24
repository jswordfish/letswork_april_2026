package com.letswork.crm.serviceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.entities.OrgHierarchy;
import com.letswork.crm.entities.User;
import com.letswork.crm.repo.UserRepo;
import com.letswork.crm.service.LetsWorkCentreService;
import com.letswork.crm.service.OrgHierarchyService;
import com.letswork.crm.service.TenantService;
import com.letswork.crm.service.UserService;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;





@Service
@Transactional
public class UserServiceImpl implements UserService{
	@Autowired
	UserRepo repo;
	
	@Autowired
	OrgHierarchyService orgHierarchyService;
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	LetsWorkCentreService letsWorkCentreService;
	
	ModelMapper mapper = new ModelMapper();
	
	String companyAdminRole = "Company Admin";
	
	
	
	
	OrgHierarchy createCompanyAdminRole(){
		OrgHierarchy hierarchy = new OrgHierarchy("Level 1000", companyAdminRole, null);
		return orgHierarchyService.saveOrUpdate(hierarchy);
	}

	@Override
	public User findByEmail(String email, String companyId) {
		// TODO Auto-generated method stub
		try {
			return repo.findByEmail(email, companyId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(email +"   "+companyId);
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public User findByEmpId(String empId, String companyId) {
		// TODO Auto-generated method stub
		return repo.findByEmpId(empId, companyId);
	}

	@Override
	public synchronized User saveOrUpdate(User user) {
		// TODO Auto-generated method stub
		
		if(!(user.getExternal() != null && user.getExternal())) {
			if(user.getOrgHierarchy() != null) {
				user.setRoleOrDesig(user.getOrgHierarchy().getRoleOrDesig());
			}
			
			if(user.getRoleOrDesig() == null || user.getRoleOrDesig().trim().length() == 0) {
				//System.out.println("2      email is "+user.getEmail()+" u.getexternal "+user.getExternal()+" role "+(user.getOrgHierarchy()==null?"null":user.getOrgHierarchy().getRoleOrDesig()));
				throw new RuntimeException("Role or designation can not be null");
			}
			
			OrgHierarchy orgHierarchy = orgHierarchyService.findByRoleOrDesig(user.getRoleOrDesig(), user.getCompanyId());
			if(orgHierarchy == null) {
				throw new RuntimeException("Role or designation invalid");
			}
			user.setOrgHierarchy(orgHierarchy);
		}
		
		
		
		User user2 = null;
			if(user.getEmail() != null && user.getEmail().trim().length() > 0) {
				user2 = findByEmail(user.getEmail(), user.getCompanyId());
			}
			else if(user.getEmpId() != null && user.getEmpId().trim().length() > 0){
				user2 = findByEmpId(user.getEmail(), user.getCompanyId());
			}
			else {
				throw new RuntimeException("No role or Emp Id present");
			}
		
			if(user2 == null) {
				
				user.setCreateDate(new Date());
				user = repo.save(user);
				
//				if(config.getSendEmail() != null && config.getSendEmail()) {
//					List<String> to = Arrays.asList(user.getEmail());
//					Map<String, String> map = new HashMap<>();
//					String first = user.getFirstName() == null?"User":user.getFirstName();
//					String last = user.getLastName()==null?"":user.getLastName();
//					String link = config.getPlatformLink().replace("${companyId}", new String(Base64.getEncoder().encode(user.getCompanyId().getBytes())));
//					String linkAdmin = config.getPlatformAdminLink().replace("${companyId}", new String(Base64.getEncoder().encode(user.getCompanyId().getBytes())));
//					map.put("fullName",first+" "+last);
//					map.put("email",user.getEmail());
//					map.put("password", user.getPassword());
////						if(user.getRoleOrDesig().equalsIgnoreCase("Company Admin")) {
////							map.put("platformLink", linkAdmin);
////						}
////						else {
////							map.put("platformLink", link);
////						}
//					map.put("platformLink", link);
//
//					
//					mailClientService.sendMail(to, null, 5471471l, map, "Onboarding you on BSRF!!!");
//				}
//				
				return user;
			}
			else {
				user.setId(user2.getId());
				user.setCreateDate(user2.getCreateDate());
				user.setUpdateDate(new Date());
				mapper.map(user, user2);
			}
		
		return repo.save(user2);
	}
	
	private String validate(User dto) {
		if(dto.getFirstName() == null || dto.getFirstName().length() == 0) {
			return "First Name Should not be null";
		}
		
		if(dto.getLastName() == null || dto.getLastName().length() == 0) {
			return "Last Name Should not be null";		
			}
		
		
		if(dto.getCompanyId() == null || dto.getCompanyId().length() == 0) {
			return "CompanyId Should not be null";	
			}
		
		if(dto.getEmail() == null || dto.getEmail().length() == 0) {
			return "Email Should not be null";	
			}
		
		if(dto.getRoleOrDesig() == null || dto.getRoleOrDesig().length() == 0) {
			return "Role Or Desig Should not be null";	
			}
		
		if(dto.getDepartment() == null || dto.getDepartment().length() == 0) {
			return "Department Should not be null";	
			}
		
		if(dto.getEmpId() == null || dto.getEmpId().length() == 0) {
			return "EmpId Should not be null";	
			}
		
		if(dto.getPassword() == null || dto.getPassword().length() == 0) {
			return "Password Should not be null";	
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
		
		
		if(orgHierarchyService.findByRoleOrDesig(dto.getRoleOrDesig(), dto.getCompanyId())==null) {
			return "This role - "+dto.getRoleOrDesig()+" does not exists";
		}
		
		
		return "ok";
	}
	
	
	@Override
	public String uploadUsersFromExcel(MultipartFile file, String companyId) throws IOException {
	    // Parse Excel file into a list of User DTOs
	    List<User> users = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, User.class);
	    
	    for(User dto : users) {
    		String val = validate(dto);
    		if(!val.equalsIgnoreCase("ok")) {
    			return val;
    		}
    	}
	    
	    List<String> responses = new ArrayList<>();

	    for (User user : users) {
	        try {
	            user.setCompanyId(companyId); // Make sure to associate company
	            user.setExternal(false);      // Assuming Excel uploads are internal users

	            // Use your existing saveOrUpdate method
	            saveOrUpdate(user);

	            responses.add("Saved or Updated: " + user.getFirstName() + " " + user.getLastName());
	        } catch (Exception e) {
	            responses.add("Error saving " + user.getEmail() + ": " + e.getMessage());
	        }
	    }

	    return "ok";
	}

	@Override
	public List<User> findAll() {
		// TODO Auto-generated method stub
		
		List<User> users = new ArrayList<>();
		Iterable<User> ire = repo.findAll();
		ire.forEach(u -> users.add(u));
		return users;
	}

	@Override
	public List<User> findUsersByRoleOrDesig(String roleOrDesig, String companyId) {
		// TODO Auto-generated method stub
		return repo.findUsersByRoleOrDesig(roleOrDesig, companyId);
	}

	@Override
	public List<User> searchUsers(String search, String companyId) {
		// TODO Auto-generated method stub
		return repo.searchUsers(search, companyId);
	}

	@Override
	public Page<User> getUsers(String companyId, Pageable pageable) {
		// TODO Auto-generated method stub
		return repo.getUsers(companyId, pageable);
	}

	@Override
	public User saveOrUpdateCompanyAdminUser(User user) {
		// TODO Auto-generated method stub
				if(user.getRoleOrDesig() == null || user.getRoleOrDesig().trim().length() == 0) {
					throw new RuntimeException("Role or designation can not be null");
				}
				
				OrgHierarchy orgHierarchy = orgHierarchyService.findByRoleOrDesig(companyAdminRole, user.getCompanyId());
				if(orgHierarchy == null) {
					orgHierarchy = createCompanyAdminRole();
				}
				user.setOrgHierarchy(orgHierarchy);
				User user2 = null;
					if(user.getEmail() != null && user.getEmail().trim().length() > 0) {
						user2 = findByEmail(user.getEmail(), user.getCompanyId());
					}
					else if(user.getEmpId() != null && user.getEmpId().trim().length() > 0){
						user2 = findByEmpId(user.getEmail(), user.getCompanyId());
					}
					else {
						throw new RuntimeException("No role or Emp Id present");
					}
				
					if(user2 == null) {
						user.setCreateDate(new Date());
						user = repo.save(user);
//						System.out.println(" config email send "+config.getSendEmail());
//						if(config.getSendEmail() != null && config.getSendEmail()) {
//							List<String> to = Arrays.asList(user.getEmail());
//							Map<String, String> map = new HashMap<>();
//							String first = user.getFirstName() == null?"User":user.getFirstName();
//							String last = user.getLastName()==null?"":user.getLastName();
//							String link = config.getPlatformLink().replace("${companyId}", new String(Base64.getEncoder().encode(user.getCompanyId().getBytes())));
//							//String linkAdmin = config.getPlatformAdminLink().replace("${companyId}", new String(Base64.getEncoder().encode(user.getCompanyId().getBytes())));
//							map.put("fullName",first+" "+last);
//							map.put("email",user.getEmail());
//							map.put("password", user.getPassword());
//							map.put("platformLink", link);
//							
//							mailClientService.sendMail(to, null, 5471471l, map, "Onboarding you as BSRF Company Admin!!");
//							System.out.println(" tenant admin creation "+config.getSendEmail()+" mail sent");
//						}
						return user;
					}
					else {
						user.setId(user2.getId());
						user.setCreateDate(user2.getCreateDate());
						user.setUpdateDate(new Date());
						mapper.map(user, user2);
					}
				
				return repo.save(user2);
	}

	@Override
	public List<User> findUsersByCompanyId(String companyId) {
		// TODO Auto-generated method stub
		return repo.findUsersByCompanyId(companyId);
	}
	
	@Override
	public Page<User> getUsers(
	        String companyId,
	        String search,
	        Pageable pageable
	) {
	    return repo.searchUsers(companyId, search, pageable);
	}

}
