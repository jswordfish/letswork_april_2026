package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.ConferenceBundleBooking;

@Repository
public interface ConferenceBundleBookingRepository
        extends JpaRepository<ConferenceBundleBooking, Long> {

    List<ConferenceBundleBooking> findByLetsWorkClientIdAndRemainingHoursGreaterThan(
            Long clientId,
            Integer hours
    );

}
