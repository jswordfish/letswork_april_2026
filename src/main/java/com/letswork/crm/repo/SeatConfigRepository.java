package com.letswork.crm.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.SeatConfig;
import com.letswork.crm.enums.SeatType;

@Repository
public interface SeatConfigRepository extends JpaRepository<SeatConfig, Long> {

    Optional<SeatConfig> findByIdAndCompanyId(Long id, String companyId);

    Page<SeatConfig> findAllByCompanyId(String companyId, Pageable pageable);

    boolean existsBySeatTypeAndLetsWorkCentreAndCityAndStateAndCompanyId(
            SeatType seatType,
            String letsWorkCentre,
            String city,
            String state,
            String companyId
    );
    
    @Query("SELECT s FROM SeatConfig s " +
            "WHERE s.companyId = :companyId " +
            "AND (:letsWorkCentre IS NULL OR s.letsWorkCentre = :letsWorkCentre) " +
            "AND (:city IS NULL OR s.city = :city) " +
            "AND (:state IS NULL OR s.state = :state) " +
            "AND (:seatType IS NULL OR s.seatType = :seatType)")
     Page<SeatConfig> searchSeatConfigs(
             @Param("companyId") String companyId,
             @Param("letsWorkCentre") String letsWorkCentre,
             @Param("city") String city,
             @Param("state") String state,
             @Param("seatType") SeatType seatType,
             Pageable pageable
     );
    
}
