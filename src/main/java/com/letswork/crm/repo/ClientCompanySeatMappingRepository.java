package com.letswork.crm.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.ClientCompanySeatMapping;
import com.letswork.crm.entities.SeatKey;
import com.letswork.crm.enums.SeatType;

@Repository
public interface ClientCompanySeatMappingRepository extends JpaRepository<ClientCompanySeatMapping, Long> {

    @Query("SELECT c FROM ClientCompanySeatMapping c WHERE c.clientCompanyName = :clientCompanyName " +
           "AND c.letsWorkCentre = :letsWorkCentre AND c.companyId = :companyId " +
           "AND c.city = :city AND c.state = :state")
    Page<ClientCompanySeatMapping> findByClientCompanyNameAndLetsWorkCentreAndCompanyIdAndCityAndState(
            @Param("clientCompanyName") String clientCompanyName,
            @Param("letsWorkCentre") String letsWorkCentre,
            @Param("companyId") String companyId,
            @Param("city") String city,
            @Param("state") String state,
            Pageable pageable);

    @Query("SELECT c FROM ClientCompanySeatMapping c WHERE c.letsWorkCentre = :letsWorkCentre " +
           "AND c.companyId = :companyId AND c.city = :city AND c.state = :state")
    Page<ClientCompanySeatMapping> findByLetsWorkCentreAndCompanyIdAndCityAndState(
            @Param("letsWorkCentre") String letsWorkCentre,
            @Param("companyId") String companyId,
            @Param("city") String city,
            @Param("state") String state,
            Pageable pageable);

    @Query("SELECT c FROM ClientCompanySeatMapping c WHERE c.companyId = :companyId")
    Page<ClientCompanySeatMapping> findByCompanyId(@Param("companyId") String companyId, Pageable pageable);
    
    @Query("SELECT c FROM ClientCompanySeatMapping c WHERE " +
    	       "c.clientCompanyName = :clientCompanyName AND " +
    	       "c.letsWorkCentre = :letsWorkCentre AND " +
    	       "c.companyId = :companyId AND " +
    	       "c.city = :city AND " +
    	       "c.state = :state AND " +
    	       "c.seatType = :seatType AND " +
    	       "c.seatNumber = :seatNumber")
    	Optional<ClientCompanySeatMapping> findByFullBusinessKey(
    	        @Param("clientCompanyName") String clientCompanyName,
    	        @Param("letsWorkCentre") String letsWorkCentre,
    	        @Param("companyId") String companyId,
    	        @Param("city") String city,
    	        @Param("state") String state,
    	        @Param("seatType") SeatType seatType,
    	        @Param("seatNumber") String seatNumber);
    
    @Query("SELECT c FROM ClientCompanySeatMapping c WHERE " +
    	       "c.seatNumber = :seatNumber AND " +
    	       "c.seatType = :seatType AND " +
    	       "c.letsWorkCentre = :letsWorkCentre AND " +
    	       "c.companyId = :companyId AND " +
    	       "c.city = :city AND " +
    	       "c.state = :state")
    	Optional<ClientCompanySeatMapping> findBySeatNumberAndSeatTypeAndLetsWorkCentreAndCompanyIdAndCityAndState(
    	        @Param("seatNumber") String seatNumber,
    	        @Param("seatType") SeatType seatType,
    	        @Param("letsWorkCentre") String letsWorkCentre,
    	        @Param("companyId") String companyId,
    	        @Param("city") String city,
    	        @Param("state") String state);
    
    @Query("SELECT new com.letswork.crm.entities.SeatKey(c.letsWorkCentre, c.city, c.state, c.companyId, c.seatType, c.seatNumber) " +
		       "FROM ClientCompanySeatMapping c WHERE c.companyId = :companyId AND c.letsWorkCentre = :letsWorkCentre " +
		       "AND c.city = :city AND c.state = :state")
		List<SeatKey> findSeatKeysByCompanyIdAndLetsWorkCentreAndCityAndState(
		        String companyId, String letsWorkCentre, String city, String state);
    
    @Query("SELECT COUNT(c) FROM ClientCompanySeatMapping c WHERE c.letsWorkCentre = :letsWorkCentre AND c.seatType = :seatType AND c.companyId = :companyId AND c.city = :city AND c.state = :state")
    long countByCompanyIdAndLetsWorkCentreAndSeatTypeAndCityAndState(
            @Param("companyId") String companyId,
            @Param("letsWorkCentre") String letsWorkCentre,
            @Param("seatType") SeatType seatType,
            @Param("city") String city,
            @Param("state") String state);
    
    @Query("SELECT c.seatNumber FROM ClientCompanySeatMapping c WHERE c.letsWorkCentre = :letsWorkCentre AND c.seatType = :seatType AND c.companyId = :companyId AND c.city = :city AND c.state = :state")
    List<String> findSeatNumbersByCompanyIdAndLetsWorkCentreAndSeatTypeAndCityAndState(
            @Param("companyId") String companyId,
            @Param("letsWorkCentre") String letsWorkCentre,
            @Param("seatType") SeatType seatType,
            @Param("city") String city,
            @Param("state") String state);
    
}
