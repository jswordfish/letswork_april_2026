package com.letswork.crm.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.UserSeatMapping;
import com.letswork.crm.enums.SeatType;

@Repository
public interface UserSeatMappingRepository extends JpaRepository<UserSeatMapping, Long> {

    Page<UserSeatMapping> findByCompanyIdAndLetsWorkCentre(String companyId, String letsWorkCentre, Pageable pageable);

    Optional<UserSeatMapping> findByEmailAndCompanyIdAndLetsWorkCentre(String email, String companyId, String letsWorkCentre);
    
    @Query("SELECT COUNT(u) FROM UserSeatMapping u WHERE u.letsWorkCentre = :letsWorkCentre AND u.seatType = :seatType AND u.companyId = :companyId")
    long countByCompanyIdAndLetsWorkCentreAndSeatType(@Param("companyId") String companyId,
                                                @Param("letsWorkCentre") String letsWorkCentre,
                                                @Param("seatType") SeatType seatType);
    
}