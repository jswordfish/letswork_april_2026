package com.letswork.crm.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Greviance;

@Repository
public interface GrevianceRepository extends JpaRepository<Greviance, Long> {

    @Query(
        "SELECT g FROM Greviance g " +
        "WHERE g.companyId = :companyId " +
        "AND (:email IS NULL OR g.email = :email) " +
        "AND (:centre IS NULL OR g.letsWorkCentre = :centre) " +
        "AND (:city IS NULL OR g.city = :city) " +
        "AND (:state IS NULL OR g.state = :state)"
    )
    Page<Greviance> filter(
            @Param("companyId") String companyId,
            @Param("email") String email,
            @Param("centre") String centre,
            @Param("city") String city,
            @Param("state") String state,
            Pageable pageable
    );
    
    Optional<Greviance> findByIdAndCompanyId(Long id, String companyId);
    
}
