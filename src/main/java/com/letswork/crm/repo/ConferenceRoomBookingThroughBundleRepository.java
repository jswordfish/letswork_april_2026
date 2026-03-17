package com.letswork.crm.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.ConferenceRoomBookingThroughBundle;

@Repository
public interface ConferenceRoomBookingThroughBundleRepository
        extends JpaRepository<ConferenceRoomBookingThroughBundle, Long> {
}
