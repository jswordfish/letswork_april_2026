package com.letswork.crm.repo;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Holiday;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    @Query("SELECT h FROM Holiday h WHERE h.letsWorkCentre = :letsWorkCentre AND h.holidayDate = :holidayDate AND h.city = :city AND h.state = :state AND h.companyId = :companyId")
    Holiday findByLetsWorkCentreAndHolidayDateAndCityAndStateAndCompanyId(
            @Param("letsWorkCentre") String letsWorkCentre,
            @Param("holidayDate") Date holidayDate,
            @Param("city") String city,
            @Param("state") String state,
            @Param("companyId") String companyId);

    @Query("SELECT h FROM Holiday h WHERE h.letsWorkCentre = :letsWorkCentre AND h.city = :city AND h.state = :state AND h.companyId = :companyId")
    List<Holiday> findByLetsWorkCentreAndCityAndStateAndCompanyId(
            @Param("letsWorkCentre") String letsWorkCentre,
            @Param("city") String city,
            @Param("state") String state,
            @Param("companyId") String companyId);
    
    @Query("SELECT h FROM Holiday h WHERE "
            + "(:companyId IS NULL OR h.companyId = :companyId) AND "
            + "(:letsWorkCentre IS NULL OR h.letsWorkCentre = :letsWorkCentre) AND "
            + "(:city IS NULL OR h.city = :city) AND "
            + "(:state IS NULL OR h.state = :state)")
       Page<Holiday> findByFilters(
               @Param("companyId") String companyId,
               @Param("letsWorkCentre") String letsWorkCentre,
               @Param("city") String city,
               @Param("state") String state,
               Pageable pageable);
}
