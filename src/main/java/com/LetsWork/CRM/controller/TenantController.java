package com.LetsWork.CRM.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LetsWork.CRM.entities.Tenant;
import com.LetsWork.CRM.entities.User;
import com.LetsWork.CRM.repo.TenantRepo;
import com.LetsWork.CRM.service.TenantService;
import com.LetsWork.CRM.service.UserService;

import jakarta.servlet.http.HttpSession;



@RestController
@CrossOrigin
public class TenantController {
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	TenantRepo repo;
	
	@Autowired
	UserService userService;
	
	
	@RequestMapping(value="createTenant",method=RequestMethod.POST)  
    public ResponseEntity<?> createTenant( @RequestBody Tenant tenant,  
           HttpSession session, @RequestParam String token) throws Exception{  
		
		if(tenantService.isCompanyIdExisting(tenant.getCompanyId())) {
			return ResponseEntity.badRequest().body("Company Id already exists!!");
		}
		
		Objects.requireNonNull(tenant);
		Objects.requireNonNull(tenant.getOrgName());
		Objects.requireNonNull(tenant.getCompanyId());
		Objects.requireNonNull(tenant.getTenantAdminEmail());
		Objects.requireNonNull(tenant.getPassword());
		tenantService.saveOrUpdate(tenant);
		
		User user = User.builder().email(tenant.getTenantAdminEmail()).password(tenant.getPassword()).department("COMPANY ADMIN").build();
		user.setCompanyId(tenant.getCompanyId());
		user.setCompanyName(tenant.getOrgName() == null?tenant.getCompanyName():tenant.getOrgName());
		user.setFirstName("Company");
		user.setLastName("Admin");
		user.setRoleOrDesig("Company Admin");
		userService.saveOrUpdateCompanyAdminUser(user);
		
		 return ResponseEntity.ok("ok");
	 }
	
	
	@RequestMapping(value="fetchTenants",method=RequestMethod.GET)  
    public ResponseEntity<?> fetchTenants( 
           HttpSession session) throws Exception{  
		
		List<Tenant> tenants = tenantService.findAll();
		 return ResponseEntity.ok(tenants);
	 }
	
	
	@DeleteMapping("/delete tenant")
	public String deleteTenant(@RequestBody Tenant tenant, @RequestParam String token) {
		
		Tenant tenant2 = tenantService.findTenantByCompanyId(tenant.getCompanyId());
		
		if(tenant2!=null) {
			repo.delete(tenant2);
			return "Tenant deleted";
		}
		
		else return "Tenant does not exists";
		
	}
	
	
}
