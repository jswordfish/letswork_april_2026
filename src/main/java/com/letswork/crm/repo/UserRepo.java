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
	
	@Query(
	        "SELECT u FROM User u " +
	        "WHERE u.companyId = :companyId " +

	        "AND (:department IS NULL OR u.department = :department) " +
	        "AND (:roleOrDesig IS NULL OR u.roleOrDesig = :roleOrDesig) " +
	        "AND (:letsWorkCentre IS NULL OR u.letsWorkCentre = :letsWorkCentre) " +
	        "AND (:city IS NULL OR u.city = :city) " +
	        "AND (:state IS NULL OR u.state = :state) " +

	        "AND ( " +
	        "   :search IS NULL " +
	        "   OR u.firstName LIKE %:search% " +
	        "   OR u.lastName LIKE %:search% " +
	        "   OR u.email LIKE %:search% " +
	        "   OR u.empId LIKE %:search% " +
	        "   OR u.roleOrDesig LIKE %:search% " +
	        "   OR u.department LIKE %:search% " +
	        "   OR u.city LIKE %:search% " +
	        "   OR u.state LIKE %:search% " +
	        ")"
	)
	Page<User> searchUsers(
	        @Param("companyId") String companyId,
	        @Param("search") String search,
	        @Param("department") String department,
	        @Param("roleOrDesig") String roleOrDesig,
	        @Param("letsWorkCentre") String letsWorkCentre,
	        @Param("city") String city,
	        @Param("state") String state,
	        Pageable pageable
	);
	
	@Query("select u from User u where u.phoneNumber =:phoneNumber and u.companyId =:companyId")
	public User findByPhoneNumber( @Param("phoneNumber") String phoneNumber,@Param("companyId") String companyId);
	
	@Query("select u from User u where u.email =:email and u.companyId =:companyId")
	public User findByEmail( @Param("email") String email,@Param("companyId") String companyId);
	
	public User findByEmail(String email);
	
	
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
