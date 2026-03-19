package com.letswork.crm.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.DayPassBundleBooking;

@Repository
public interface DayPassBundleBookingRepository extends JpaRepository<DayPassBundleBooking, Long> {

}
