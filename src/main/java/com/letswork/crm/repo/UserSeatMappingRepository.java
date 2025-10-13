package com.LetsWork.CRM.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.LetsWork.CRM.entities.UserSeatMapping;
import com.LetsWork.CRM.enums.SeatType;

@Repository
public interface UserSeatMappingRepository extends JpaRepository<UserSeatMapping, Long> {

    Page<UserSeatMapping> findByCompanyIdAndLocation(String companyId, String location, Pageable pageable);

    Optional<UserSeatMapping> findByEmailAndCompanyIdAndLocation(String email, String companyId, String location);
    
    @Query("SELECT COUNT(u) FROM UserSeatMapping u WHERE u.location = :location AND u.seatType = :seatType AND u.companyId = :companyId")
    long countByCompanyIdAndLocationAndSeatType(@Param("companyId") String companyId,
                                                @Param("location") String location,
                                                @Param("seatType") SeatType seatType);
    
}