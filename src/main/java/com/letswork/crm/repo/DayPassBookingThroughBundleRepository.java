package com.letswork.crm.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.DayPassBookingThroughBundle;

@Repository
public interface DayPassBookingThroughBundleRepository extends JpaRepository<DayPassBookingThroughBundle, Long> {
}
