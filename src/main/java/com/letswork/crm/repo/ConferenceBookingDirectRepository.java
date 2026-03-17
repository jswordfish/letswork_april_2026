package com.letswork.crm.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.ConferenceBookingDirect;

@Repository
public interface ConferenceBookingDirectRepository
        extends JpaRepository<ConferenceBookingDirect, Long> {
}
