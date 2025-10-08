package com.LetsWork.CRM.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LetsWork.CRM.entities.OrgHierarchy;
import com.LetsWork.CRM.repo.OrgHierarchyRepo;
import com.LetsWork.CRM.repo.UserRepo;
import com.LetsWork.CRM.service.OrgHierarchyService;
import com.LetsWork.CRM.service.UserService;


@RestController
@CrossOrigin
public class OrgHierarchyController {
	
	@Autowired
	OrgHierarchyService service;
	
	@Autowired
	OrgHierarchyRepo repo;
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserRepo userRepo;
	
	@PostMapping("/Create OrgHierarchy")
	public void createOrgHierarchy(@RequestBody OrgHierarchy orghierarchy, @RequestParam String token) {
		
		service.saveOrUpdate(orghierarchy);
		
	}
	
	
	@DeleteMapping("/delete role")
	public String deleteOrgHierarchy(@RequestBody OrgHierarchy orghierarchy, @RequestParam String token) {
		
		OrgHierarchy org = service.findByRoleOrDesig(orghierarchy.getRoleOrDesig(),orghierarchy.getCompanyId());
		
//		List<User> list = userService.findUsersByRoleOrDesig(orghierarchy.getRoleOrDesig(),orghierarchy.getCompanyId());
		
		Long count = userRepo.countUsersByRoleOrDesig(orghierarchy.getRoleOrDesig(),orghierarchy.getCompanyId());
		
		if(org!=null && count<1) {
			repo.delete(org);
			return "Role deleted";
		}
		
		else if(count>=1) {
			return "This role is still associated with "+count+" users";
		}
		
		else return "Role does not exists";
		
	}
	
	
	@GetMapping("find all levels")
	public List<OrgHierarchy> listByCompanyId(@RequestParam String companyId){
		return service.getAllHierarchyLevels(companyId);
	}
	

}
