package com.letswork.crm.repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.BookDayPass;

@Repository
public interface BookDayPassRepository extends JpaRepository<BookDayPass, Long> {

    List<BookDayPass> findByCompanyId(String companyId);

    List<BookDayPass> findByEmailAndCompanyId(String email, String companyId);

    List<BookDayPass> findByLetsWorkCentreAndCompanyId(String letsWorkCentre, String companyId);
    
    Optional<BookDayPass> findByBookingCode(String bookingCode);

    @Query("SELECT b FROM BookDayPass b " +
    	       "WHERE b.companyId = :companyId " +
    	       "AND (:email IS NULL OR b.email = :email) " +
    	       "AND (:centre IS NULL OR b.letsWorkCentre = :centre) " +
    	       "AND (:city IS NULL OR b.city = :city) " +
    	       "AND (:state IS NULL OR b.state = :state) " +
    	       "AND (:date IS NULL OR b.dateOfBooking = :date)")
    	List<BookDayPass> filter(
    	        @Param("companyId") String companyId,
    	        @Param("email") String email,
    	        @Param("centre") String centre,
    	        @Param("city") String city,
    	        @Param("state") String state,
    	        @Param("date") LocalDate date
    	);
}
