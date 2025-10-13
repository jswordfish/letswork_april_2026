package com.letswork.crm.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ConferenceRoom;


public interface ConferenceRoomService {
	
	String saveOrUpdate(ConferenceRoom conferenceRoom);
	
	List<ConferenceRoom> findByLocation(String location, String companyId);
	
	PaginatedResponseDto findByLocation(String location, String companyId, int page);
	
//	PaginatedResponseDto findAvailableConferenceRooms(Boolean available, int page);
	
	String deleteByName(ConferenceRoom conferenceRoom);
	
	public List<String> uploadConferenceRooms(MultipartFile file, String companyId) throws IOException;

}
