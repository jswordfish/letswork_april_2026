package com.LetsWork.CRM.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.LetsWork.CRM.entities.Visitor;



@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {

   
    List<Visitor> findByVisitDate(LocalDate visitDate);
    
    @Query("SELECT v FROM Visitor v WHERE v.visitDate = :visitDate AND v.companyId = :companyId")
    Page<Visitor> findByVisitDate(@Param("visitDate") LocalDate visitDate,
                                  @Param("companyId") String companyId,
                                  Pageable pageable);

    
    List<Visitor> findByNameContainingIgnoreCase(String name);
    
    
    Visitor findByNameAndEmail(String name, String email);
    
}
