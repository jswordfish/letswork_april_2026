package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.BuyConferenceBundle;

@Repository
public interface BuyConferenceBundleRepository
        extends JpaRepository<BuyConferenceBundle, Long> {

    List<BuyConferenceBundle> findByCompanyId(String companyId);

    List<BuyConferenceBundle> findByEmailAndCompanyId(
            String email,
            String companyId
    );

    List<BuyConferenceBundle> findByBundleIdAndCompanyId(
            Long bundleId,
            String companyId
    );

    @Query(
        "SELECT b FROM BuyConferenceBundle b " +
        "WHERE b.companyId = :companyId " +
        "AND (:email IS NULL OR b.email = :email) " +
        "AND (:bundleId IS NULL OR b.bundleId = :bundleId)"
    )
    List<BuyConferenceBundle> findByFilters(
            @Param("companyId") String companyId,
            @Param("email") String email,
            @Param("bundleId") Long bundleId
    );
}
