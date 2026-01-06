package com.letswork.crm.repo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.ConferenceRoomTimeSlot;

@Repository
public interface ConferenceRoomTimeSlotRepository
        extends JpaRepository<ConferenceRoomTimeSlot, Long> {

	@Query("SELECT t FROM ConferenceRoomTimeSlot t " +
		       "WHERE t.companyId = :companyId " +
		       "AND t.letsWorkCentre = :centre " +
		       "AND t.city = :city " +
		       "AND t.state = :state " +
		       "AND t.roomName = :roomName " +
		       "AND t.slotDate = :date " +
		       "ORDER BY t.startTime")
    List<ConferenceRoomTimeSlot> findBookedSlots(
            @Param("companyId") String companyId,
            @Param("centre") String centre,
            @Param("city") String city,
            @Param("state") String state,
            @Param("roomName") String roomName,
            @Param("date") LocalDate date
    );

    boolean existsByCompanyIdAndLetsWorkCentreAndCityAndStateAndRoomNameAndSlotDateAndStartTime(
            String companyId,
            String letsWorkCentre,
            String city,
            String state,
            String roomName,
            LocalDate slotDate,
            LocalTime startTime
    );
}