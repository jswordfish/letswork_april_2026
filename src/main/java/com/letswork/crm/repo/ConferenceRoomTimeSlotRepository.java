package com.letswork.crm.repo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.ConferenceBookingDirect;
import com.letswork.crm.entities.ConferenceRoomBookingThroughBundle;
import com.letswork.crm.entities.ConferenceRoomTimeSlot;

@Repository
public interface ConferenceRoomTimeSlotRepository
        extends JpaRepository<ConferenceRoomTimeSlot, Long> {

	@Query("SELECT t FROM ConferenceRoomTimeSlot t " +
		       "WHERE t.companyId = :companyId " +
		       "AND t.letsWorkCentre.id = :centreId " +
		       "AND t.conferenceRoom.id = :roomId " +
		       "AND t.slotDate = :date " +
		       "AND (t.softDelete  IS null OR t.softDelete = false) " +
		       "ORDER BY t.startTime")
		List<ConferenceRoomTimeSlot> findBookedSlots(
		        @Param("companyId") String companyId,
		        @Param("centreId") Long centreId,
		        @Param("roomId") Long roomId,
		        @Param("date") LocalDate date
		);
	
	void deleteByBooking(ConferenceBookingDirect booking);
	
	void deleteByBooking(ConferenceRoomBookingThroughBundle booking);
	
	@Query("SELECT COUNT(c) > 0 FROM ConferenceRoomTimeSlot c " +
		       "WHERE c.companyId = :companyId " +
		       "AND c.letsWorkCentre.name = :letsWorkCentre " +
		       "AND c.letsWorkCentre.city = :city " +
		       "AND c.letsWorkCentre.state = :state " +
		       "AND c.conferenceRoom.name = :roomName " +
		       "AND c.slotDate = :slotDate " +
		       "AND c.startTime = :startTime " +
		       "AND c.softDelete = false")
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