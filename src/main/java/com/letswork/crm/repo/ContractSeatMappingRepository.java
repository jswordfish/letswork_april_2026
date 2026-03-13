package com.letswork.crm.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.ContractSeatMapping;
import com.letswork.crm.entities.SeatKey;
import com.letswork.crm.enums.SeatType;

@Repository
public interface ContractSeatMappingRepository extends JpaRepository<ContractSeatMapping, Long> {

	@Query("SELECT c FROM ContractSeatMapping c " +
 	       "WHERE c.companyId = :companyId " +
 	       "AND c.contract.letsWorkCentre.name = :letsWorkCentre " +
 	       "AND c.contract.letsWorkCentre.city = :city " +
 	       "AND c.contract.letsWorkCentre.state = :state " +
 	       "AND (c.seat.seatNumber =:seatNumber) " +
 	       "AND (c.seat.seatType =:seatType)") 
    Optional<ContractSeatMapping> findBySeatNumberAndSeatTypeAndLetsWorkCentreAndCompanyIdAndCityAndState(
    		@Param("seatNumber") String seatNumber,
    		@Param("seatType") String seatType,
    		@Param("letsWorkCentre") String letsWorkCentre,
            @Param("companyId") String companyId,
            @Param("city") String city,
            @Param("state") String state
    );
    
    @Query("SELECT c FROM ContractSeatMapping c " +
    	       "WHERE c.companyId = :companyId " +
    	       "AND c.contract.letsWorkCentre.name = :letsWorkCentre " +
    	       "AND c.contract.letsWorkCentre.city = :city " +
    	       "AND c.contract.letsWorkCentre.state = :state " +
    	       "AND (c.deleted IS NULL OR c.deleted = false) " +
    	       "AND (c.actualEndDate IS NULL OR c.actualEndDate >= CURRENT_DATE)")
    	List<ContractSeatMapping> findActiveByLocation(
    	        @Param("companyId") String companyId,
    	        @Param("letsWorkCentre") String letsWorkCentre,
    	        @Param("city") String city,
    	        @Param("state") String state
    	);

    @Query("SELECT c FROM ContractSeatMapping c " +
            "WHERE c.contract.id = :contractId " +
            "AND c.companyId = :companyId")
     List<ContractSeatMapping> findByContractIdAndCompanyId(
             @Param("contractId") Long contractId, 
             @Param("companyId") String companyId
     );

     @Query("SELECT c FROM ContractSeatMapping c " +
            "WHERE c.contract.id = :contractId " +
            "AND c.contract.letsWorkCentre.name = :letsWorkCentre " +
            "AND c.companyId = :companyId " +
            "AND c.contract.letsWorkCentre.city = :city " +
            "AND c.contract.letsWorkCentre.state = :state " +
            "AND c.seat.seatType = :seatType " +
            "AND c.seat.seatNumber = :seatNumber")
     Optional<ContractSeatMapping> findByFullBusinessKey(
             @Param("contractId") Long contractId,
             @Param("letsWorkCentre") String letsWorkCentre,
             @Param("companyId") String companyId,
             @Param("city") String city,
             @Param("state") String state,
             @Param("seatType") SeatType seatType,
             @Param("seatNumber") String seatNumber
     );
     
     @Query("SELECT DISTINCT new com.letswork.crm.entities.SeatKey(" +
             "c.contract.letsWorkCentre.name, c.contract.letsWorkCentre.city, c.contract.letsWorkCentre.state, c.companyId, c.seat.seatType, c.seat.seatNumber) " +
             "FROM ContractSeatMapping c " +
             "WHERE c.companyId = :companyId AND c.contract.letsWorkCentre.name = :letsWorkCentre " +
             "AND c.contract.letsWorkCentre.city = :city AND c.contract.letsWorkCentre.state = :state")
      List<SeatKey> findSeatKeysByCompanyIdAndLetsWorkCentreAndCityAndState(
              @Param("companyId") String companyId,
              @Param("letsWorkCentre") String letsWorkCentre,
              @Param("city") String city,
              @Param("state") String state
      );
     
     @Query("SELECT c FROM ContractSeatMapping c " +
    	       "WHERE c.companyId = :companyId " +
    	       "AND c.contract.letsWorkCentre.name = :letsWorkCentre " +
    	       "AND c.contract.letsWorkCentre.city = :city " +
    	       "AND c.contract.letsWorkCentre.state = :state " +
    	       "AND c.seat.seatType = :seatType " +
    	       "AND c.seat.seatNumber = :seatNumber " +
    	       "AND (c.deleted IS NULL OR c.deleted = false) " +
    	       "AND (c.actualEndDate IS NULL OR c.actualEndDate >= CURRENT_DATE)")
    	Optional<ContractSeatMapping> findActiveBySeatKey(
    	        @Param("companyId") String companyId,
    	        @Param("letsWorkCentre") String letsWorkCentre,
    	        @Param("city") String city,
    	        @Param("state") String state,
    	        @Param("seatType") SeatType seatType,
    	        @Param("seatNumber") String seatNumber
    	);
     
     
     
}
