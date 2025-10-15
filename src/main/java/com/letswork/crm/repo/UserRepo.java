package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.User;




@Repository
public interface UserRepo extends CrudRepository<User, Long> {
	
	@Query("select u from User u where u.email =:email and u.companyId =:companyId")
	public User findByEmail( @Param("email") String email,@Param("companyId") String companyId);
	
	
	@Query("select u from User u where u.empId =:empId and u.companyId =:companyId")
	public User findByEmpId(@Param("empId") String empId,@Param("companyId") String companyId);
	
	
	@Query(value = "select u from User u join u.orgHierarchy o where o.roleOrDesig=:roleOrDesig and o.companyId =:companyId and (u.external is null or u.external =false)")
	 public List<User> findUsersByRoleOrDesig( @Param("roleOrDesig") String roleOrDesig, @Param("companyId") String companyId);
	
	
	//@Query(value="SELECT  u FROM User u WHERE ( u.companyId =:companyId) and ( (lower(u.firstName) LIKE lower(CONCAT('%',:search,'%')) OR (lower(u.lastName) LIKE lower(CONCAT('%',:search,'%')) OR (lower(u.location) LIKE lower(CONCAT('%',:search,'%')) OR (lower(u.email) LIKE lower(CONCAT('%',:search,'%')) OR (lower(u.empId) LIKE lower(CONCAT('%',:search,'%')) OR (lower(u.department) LIKE lower(CONCAT('%',:search,'%')) ) ")	
	// 
	@Query(value="SELECT  u FROM User u WHERE ( u.companyId =:companyId) and  ( lower(u.firstName) LIKE lower(CONCAT('%',:search,'%')) OR lower(u.lastName) LIKE lower(CONCAT('%',:search,'%')) OR lower(u.letsWorkCentre) LIKE lower(CONCAT('%',:search,'%')) OR lower(u.email) LIKE lower(CONCAT('%',:search,'%')) OR lower(u.empId) LIKE lower(CONCAT('%',:search,'%')) OR lower(u.department) LIKE lower(CONCAT('%',:search,'%')) )  and (u.external is null or u.external =false)")
	public List<User> searchUsers(@Param("search") String search, @Param("companyId") String companyId);
	
	
	@Query(value="SELECT l FROM User l WHERE l.companyId=:companyId  and (l.external is null  or l.external =false)")
	public Page<User> getUsers(@Param("companyId") String companyId, Pageable pageable);
	
	
	@Query(value="SELECT l FROM User l WHERE l.companyId=:companyId   and (l.external is null or l.external =false)")
	 public List<User> findUsersByCompanyId( @Param("companyId") String companyId);
	
	@Query("SELECT COUNT(u) FROM User u JOIN u.orgHierarchy o " +
		       "WHERE o.roleOrDesig = :roleOrDesig " +
		       "AND o.companyId = :companyId " +
		       "AND (u.external IS NULL OR u.external = false)")
		public Long countUsersByRoleOrDesig(@Param("roleOrDesig") String roleOrDesig, @Param("companyId") String companyId);
	
}
