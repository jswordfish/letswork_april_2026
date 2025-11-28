package com.letswork.crm.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.ConferenceRoomAmenityMapping;

@Repository
public interface ConferenceRoomAmenityMappingRepository
        extends JpaRepository<ConferenceRoomAmenityMapping, Long> {

    List<ConferenceRoomAmenityMapping> findByRoomId(Long roomId);

    ConferenceRoomAmenityMapping findByRoomIdAndAmenityId(Long roomId, Long amenityId);
    
}
