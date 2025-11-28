package com.letswork.crm.service;

import java.util.List;

import com.letswork.crm.entities.Amenities;
import com.letswork.crm.entities.ConferenceRoomAmenityMapping;

public interface ConferenceRoomAmenityMappingService {
	
	ConferenceRoomAmenityMapping assignAmenity(Long roomId, Long amenityId);

    List<Amenities> getAmenitiesForRoom(Long roomId);

    void removeAmenity(Long mappingId);

}
