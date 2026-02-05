package com.letswork.crm.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.ContractSeatMapping;
import com.letswork.crm.enums.SeatType;

@Repository
public interface ContractSeatMappingRepository extends JpaRepository<ContractSeatMapping, Long> {

    Optional<ContractSeatMapping> findBySeatNumberAndSeatTypeAndLetsWorkCentreAndCompanyIdAndCityAndState(
            String seatNumber,
            SeatType seatType,
            String letsWorkCentre,
            String companyId,
            String city,
            String state
    );

    @Query("SELECT c FROM ContractSeatMapping c " +
            "WHERE c.contractId = :contractId " +
            "AND c.companyId = :companyId")
     List<ContractSeatMapping> findByContractIdAndCompanyId(
             @Param("contractId") Long contractId, 
             @Param("companyId") String companyId
     );

     @Query("SELECT c FROM ContractSeatMapping c " +
            "WHERE c.contractId = :contractId " +
            "AND c.letsWorkCentre = :letsWorkCentre " +
            "AND c.companyId = :companyId " +
            "AND c.city = :city " +
            "AND c.state = :state " +
            "AND c.seatType = :seatType " +
            "AND c.seatNumber = :seatNumber")
     Optional<ContractSeatMapping> findByFullBusinessKey(
             @Param("contractId") Long contractId,
             @Param("letsWorkCentre") String letsWorkCentre,
             @Param("companyId") String companyId,
             @Param("city") String city,
             @Param("state") String state,
             @Param("seatType") SeatType seatType,
             @Param("seatNumber") String seatNumber
     );
}
