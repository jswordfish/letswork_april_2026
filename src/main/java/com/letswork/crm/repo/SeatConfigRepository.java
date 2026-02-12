package com.letswork.crm.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
