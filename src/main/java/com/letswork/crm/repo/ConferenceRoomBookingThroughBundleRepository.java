package com.letswork.crm.repo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.ConferenceRoomBookingThroughBundle;
import com.letswork.crm.entities.LetsWorkClient;
import com.letswork.crm.enums.BookingStatus;

@Repository
public interface ConferenceRoomBookingThroughBundleRepository
        extends JpaRepository<ConferenceRoomBookingThroughBundle, Long> {
	
	List<ConferenceRoomBookingThroughBundle> findByLetsWorkClient(LetsWorkClient client);
	
	@Query("SELECT b FROM Booking b " +
		       "LEFT JOIN b.letsWorkCentre c " +
		       "LEFT JOIN b.conferenceRoom r " +
		       "WHERE TYPE(b) = ConferenceRoomBookingThroughBundle " +
		       "AND (:companyId IS NULL OR b.companyId = :companyId) " +
		       "AND (:clientId IS NULL OR (b.letsWorkClient IS NOT NULL AND b.letsWorkClient.id = :clientId)) " +
		       "AND (:status IS NULL OR b.bookingStatus = :status) " +
		       "AND (:centre IS NULL OR c.name = :centre) " +
		       "AND (:city IS NULL OR c.city = :city) " +
		       "AND (:state IS NULL OR c.state = :state) " +
		       "AND (:roomName IS NULL OR r.name = :roomName) " +
		       "AND (:fromDate IS NULL OR b.dateOfPurchase >= :fromDate) " +
		       "AND (:toDate IS NULL OR b.dateOfPurchase <= :toDate) " +
		       "AND (:minHours IS NULL OR b.numberOfHours >= :minHours) " +
		       "AND (:maxHours IS NULL OR b.numberOfHours <= :maxHours)")
	    Page<ConferenceRoomBookingThroughBundle> filter(
	            @Param("companyId") String companyId,
	            @Param("clientId") Long clientId,
	            @Param("status") BookingStatus status,
	            @Param("centre") String centre,
	            @Param("city") String city,
	            @Param("state") String state,
	            @Param("roomName") String roomName,
	            @Param("fromDate") java.time.LocalDateTime fromDate,
	            @Param("toDate") java.time.LocalDateTime toDate,
	            @Param("minHours") Float minHours,
	            @Param("maxHours") Float maxHours,
	            Pageable pageable
	    );
	
	@Query("SELECT b FROM ConferenceRoomBookingThroughBundle b " +
		       "JOIN b.slots s " +
		       "WHERE b.bookingStatus = 'ACTIVE' " +
		       "GROUP BY b.id, b.startDate " +
		       "HAVING b.startDate < :today " +
		       "OR (b.startDate = :today AND MAX(s.endTime) <= :now)")
		List<ConferenceRoomBookingThroughBundle> findExpiredBookings(
		        @Param("today") LocalDate today,
		        @Param("now") LocalTime now
		);
	
	Optional<ConferenceRoomBookingThroughBundle> findByIdAndCompanyId(Long id, String companyId);
	
}
