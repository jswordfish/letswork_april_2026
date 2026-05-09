package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.DayPassBundle;

@Repository
public interface DayPassBundleRepository
        extends JpaRepository<DayPassBundle, Long> {
	
	@Query("SELECT d FROM DayPassBundle d " +
	 	       "WHERE d.companyId = :companyId " +
	 	       "AND d.letsWorkCentre.name = :letsWorkCentre " +
	 	       "AND d.letsWorkCentre.city = :city " +
	 	       "AND d.letsWorkCentre.state = :state ")
	 	       
    List<DayPassBundle> findByLetsWorkCentreAndCompanyIdAndCityAndState(
            @Param("letsWorkCentre")String letsWorkCentre,
            @Param("companyId")String companyId,
            @Param("city")String city,
            @Param("state")String state
    );
	
	@Query("SELECT d FROM DayPassBundle d " +
		       "WHERE (:companyId IS NULL OR d.companyId = :companyId) " +
		       "AND (:letsWorkCentre IS NULL OR d.letsWorkCentre.name = :letsWorkCentre) " +
		       "AND (:city IS NULL OR d.letsWorkCentre.city = :city) " +
		       "AND (:state IS NULL OR d.letsWorkCentre.state = :state) " +
		       "AND (:numberOfDays IS NULL OR d.numberOfDays = :numberOfDays) " +
		       "AND (:validForDays IS NULL OR d.validForDays = :validForDays) " +
		       "AND (:discountPercentage IS NULL OR d.discountPercentage = :discountPercentage) " +
		       "AND (:price IS NULL OR d.price = :price) " +
		       "AND (" +
		       "  :search IS NULL " +
		       "  OR LOWER(d.companyId) LIKE LOWER(CONCAT('%', CONCAT(:search, '%'))) " +
		       "  OR LOWER(d.letsWorkCentre.name) LIKE LOWER(CONCAT('%', CONCAT(:search, '%'))) " +
		       "  OR LOWER(d.letsWorkCentre.city) LIKE LOWER(CONCAT('%', CONCAT(:search, '%'))) " +
		       "  OR LOWER(d.letsWorkCentre.state) LIKE LOWER(CONCAT('%', CONCAT(:search, '%'))) " +
		       "  OR CAST(d.numberOfDays AS string) LIKE CONCAT('%', CONCAT(:search, '%')) " +
		       "  OR CAST(d.validForDays AS string) LIKE CONCAT('%', CONCAT(:search, '%')) " +
		       "  OR CAST(d.discountPercentage AS string) LIKE CONCAT('%', CONCAT(:search, '%')) " +
		       "  OR CAST(d.price AS string) LIKE CONCAT('%', CONCAT(:search, '%')) " +
		       ")")
		List<DayPassBundle> searchBundles(
		        @Param("companyId") String companyId,
		        @Param("letsWorkCentre") String letsWorkCentre,
		        @Param("city") String city,
		        @Param("state") String state,
		        @Param("numberOfDays") Integer numberOfDays,
		        @Param("validForDays") Integer validForDays,
		        @Param("discountPercentage") Integer discountPercentage,
		        @Param("price") float price,
		        @Param("search") String search
		);
    
	@Query("SELECT d FROM DayPassBundle d " +
	 	       "WHERE d.companyId = :companyId " +
	 	       "AND d.letsWorkCentre.name = :letsWorkCentre " +
	 	       "AND d.letsWorkCentre.city = :city " +
	 	       "AND d.letsWorkCentre.state = :state " +
	 	       "AND d.numberOfDays = :numberOfDays")
    DayPassBundle findByLetsWorkCentreAndCompanyIdAndCityAndStateAndNumberOfDays(@Param("letsWorkCentre")String letsWorkCentre,
    		@Param("companyId")String companyId,
    		@Param("city")String city,
    		@Param("state")String state,
    		@Param("numberOfDays")Integer numberOfDays);

    List<DayPassBundle> findAllByCompanyId(String companyId);
}
