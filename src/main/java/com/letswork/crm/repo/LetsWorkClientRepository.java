package com.letswork.crm.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.LetsWorkClient;



@Repository
public interface LetsWorkClientRepository extends JpaRepository<LetsWorkClient, Long> {
	
	Optional<LetsWorkClient> findByEmailAndCompanyId(String email, String companyId);
	
    List<LetsWorkClient>
        findByUserIdAndCompanyId(
                Long userId,
                String companyId
        );

    Optional<LetsWorkClient>
        findByIdAndCompanyId(
                Long id,
                String companyId
        );

    boolean existsByClientCompanyNameAndCompanyId(
            String clientCompanyName,
            String companyId
    );

	@Query("SELECT c FROM LetsWorkClient c WHERE " +
		       "c.clientCompanyName = :companyName AND " +
		       "c.companyId = :companyId AND " +
		       "c.city = :city AND " +
		       "c.state = :state AND " +
		       "c.letsWorkCentre = :letsWorkCentre")
		LetsWorkClient findByClientCompanyNameAndCompanyIdAndCityAndStateAndLetsWorkCentre(
		        @Param("companyName") String companyName,
		        @Param("companyId") String companyId,
		        @Param("city") String city,
		        @Param("state") String state,
		        @Param("letsWorkCentre") String letsWorkCentre);
	
	@Query(
		    "SELECT c FROM LetsWorkClient c " +
		    "WHERE c.companyId = :companyId " +
		    "AND (:letsWorkCentre IS NULL OR c.letsWorkCentre = :letsWorkCentre) " +
		    "AND (:city IS NULL OR c.city = :city) " +
		    "AND (:state IS NULL OR c.state = :state) " +
		    "AND (:category IS NULL OR c.category = :category) " +
		    "AND (:subCategory IS NULL OR c.subCategory = :subCategory) " +
		    "AND (" +
		    "     :search IS NULL " +
		    "     OR c.clientCompanyName LIKE %:search% " +
		    "     OR c.letsWorkCentre LIKE %:search% " +
		    "     OR c.city LIKE %:search% " +
		    "     OR c.state LIKE %:search%" +
		    ")"
		)
		Page<LetsWorkClient> searchClientCompanies(
		        @Param("companyId") String companyId,
		        @Param("letsWorkCentre") String letsWorkCentre,
		        @Param("city") String city,
		        @Param("state") String state,
		        @Param("category") String category,
		        @Param("subCategory") String subCategory,
		        @Param("search") String search,
		        Pageable pageable
		);

	@Query("SELECT c FROM LetsWorkClient c WHERE c.letsWorkCentre = :letsWorkCentre AND c.companyId = :companyId AND c.city = :city AND c.state = :state")
	List<LetsWorkClient> findByLetsWorkCentreAndCompanyIdAndCityAndState(@Param("letsWorkCentre") String letsWorkCentre,
	                                                                    @Param("companyId") String companyId,
	                                                                    @Param("city") String city,
	                                                                    @Param("state") String state);

	@Query("SELECT c FROM LetsWorkClient c WHERE c.letsWorkCentre = :letsWorkCentre AND c.companyId = :companyId AND c.city = :city AND c.state = :state")
	Page<LetsWorkClient> findByLetsWorkCentreAndCompanyIdAndCityAndState(@Param("letsWorkCentre") String letsWorkCentre,
	                                                                    @Param("companyId") String companyId,
	                                                                    @Param("city") String city,
	                                                                    @Param("state") String state,
	                                                                    Pageable pageable);
	
	Page<LetsWorkClient> findByCompanyId(String companyId, Pageable pageable);
    
    
}
