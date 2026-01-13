package com.letswork.crm.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Visitor;



@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {

   
    List<Visitor> findByVisitDate(LocalDate visitDate);
    
    @Query("SELECT v FROM Visitor v WHERE v.visitDate = :visitDate AND v.companyId = :companyId")
    Page<Visitor> findByVisitDate(@Param("visitDate") LocalDate visitDate,
                                  @Param("companyId") String companyId,
                                  Pageable pageable);

    
    List<Visitor> findByNameContainingIgnoreCase(String name);
    
    
    Visitor findByNameAndEmail(String name, String email);
    
    Visitor findByEmailOfVisitorAndEmailAndCompanyIdAndVisitDate(String emailOfVisitor, String email, String companyId, LocalDate visitDate);
    
    List<Visitor> findByNameAndEmailOfVisitorAndCompanyId(String name, String emailOfVisitor, String companyId);
    
    Visitor findByCompanyIdAndEmailOfVisitorAndVisitDate(
            String companyId,
            String emailOfVisitor,
            LocalDate visitDate
    );

    @Query(
    	    "SELECT v FROM Visitor v " +
    	    "WHERE v.companyId = :companyId " +
    	    "AND (:name IS NULL OR v.name = :name) " +
    	    "AND (:email IS NULL OR v.email = :email) " +
    	    "AND (:emailOfVisitor IS NULL OR v.emailOfVisitor = :emailOfVisitor) " +
    	    "AND (:visitDate IS NULL OR v.visitDate = :visitDate) " +
    	    "AND (:centre IS NULL OR v.letsWorkCentre = :centre) " +
    	    "AND (:city IS NULL OR v.city = :city) " +
    	    "AND (:state IS NULL OR v.state = :state)"
    	)
    	Page<Visitor> filter(
    	        @Param("companyId") String companyId,
    	        @Param("name") String name,
    	        @Param("email") String email,
    	        @Param("emailOfVisitor") String emailOfVisitor,
    	        @Param("visitDate") LocalDate visitDate,
    	        @Param("centre") String centre,
    	        @Param("city") String city,
    	        @Param("state") String state,
    	        Pageable pageable
    	);
    
    Visitor findByBookingCode(String bookingCode);
    
}
