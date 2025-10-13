package com.letswork.crm.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.entities.User;





public interface UserService {
	
	public User findByEmail(String email, String companyId);
	
	
	public User findByEmpId(String empId, String companyId);
	
	public User saveOrUpdate(User user);
	
	
	public User saveOrUpdateCompanyAdminUser(User user);
	
	public List<User> findAll();
	
	public List<User> findUsersByRoleOrDesig( String roleOrDesig, String companyId);
	
	public List<User> searchUsers(String search, String companyId);
	
	public Page<User> getUsers(String companyId, Pageable pageable);
	
	public List<User> findUsersByCompanyId(  String companyId);
	
	public List<String> uploadUsersFromExcel(MultipartFile file, String companyId) throws IOException;
	

}
