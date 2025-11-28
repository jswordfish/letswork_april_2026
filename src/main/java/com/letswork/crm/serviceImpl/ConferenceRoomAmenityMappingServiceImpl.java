package com.letswork.crm.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.letswork.crm.entities.Amenities;
import com.letswork.crm.entities.ConferenceRoom;
import com.letswork.crm.entities.ConferenceRoomAmenityMapping;
import com.letswork.crm.enums.AmenityType;
import com.letswork.crm.repo.AmenitiesRepository;
import com.letswork.crm.repo.ConferenceRoomAmenityMappingRepository;
import com.letswork.crm.repo.ConferenceRoomRepository;
import com.letswork.crm.service.ConferenceRoomAmenityMappingService;

@Service
@Transactional
public class ConferenceRoomAmenityMappingServiceImpl implements ConferenceRoomAmenityMappingService {

    @Autowired
    private ConferenceRoomAmenityMappingRepository repo;

    @Autowired
    private ConferenceRoomRepository roomRepo;

    @Autowired
    private AmenitiesRepository amenityRepo;

    @Override
    public ConferenceRoomAmenityMapping assignAmenity(Long roomId, Long amenityId) {

        ConferenceRoom room = roomRepo.findById(roomId)
                .orElseThrow(() -> new RuntimeException("ConferenceRoom not found with id: " + roomId));

        Amenities amenity = amenityRepo.findById(amenityId)
                .orElseThrow(() -> new RuntimeException("Amenity not found with id: " + amenityId));

        // ❗ VALIDATION: only conference room amenities allowed
        if (amenity.getAmenityType() != AmenityType.CONFERENCE_ROOM) {
            throw new RuntimeException("Only CONFERENCE_ROOM type amenities are allowed.");
        }

        // prevent duplicate mapping
        ConferenceRoomAmenityMapping existing =
                repo.findByRoomIdAndAmenityId(roomId, amenityId);

        if (existing != null) {
            return existing; // already mapped
        }

        ConferenceRoomAmenityMapping mapping = new ConferenceRoomAmenityMapping();
        mapping.setRoom(room);
        mapping.setAmenity(amenity);

        return repo.save(mapping);
    }

    @Override
    public List<Amenities> getAmenitiesForRoom(Long roomId) {
        List<ConferenceRoomAmenityMapping> mappings = repo.findByRoomId(roomId);

        return mappings.stream()
                .map(ConferenceRoomAmenityMapping::getAmenity)
                .collect(Collectors.toList());
    }

    @Override
    public void removeAmenity(Long mappingId) {
        if (!repo.existsById(mappingId)) {
            throw new RuntimeException("Mapping not found with id: " + mappingId);
        }
        repo.deleteById(mappingId);
    }
}
