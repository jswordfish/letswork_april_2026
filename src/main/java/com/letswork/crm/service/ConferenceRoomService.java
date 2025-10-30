package com.letswork.crm.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.ConferenceRoom;


public interface ConferenceRoomService {
	
	String saveOrUpdate(ConferenceRoom conferenceRoom);
	
	List<ConferenceRoom> findByLetsWorkCentre(String letsWorkCentre, String companyId, String city, String state);
	
	PaginatedResponseDto findByLetsWorkCentre(String letsWorkCentre, String companyId, String city, String state, int page);
	
//	PaginatedResponseDto findAvailableConferenceRooms(Boolean available, int page);
	
	String deleteByName(ConferenceRoom conferenceRoom);
	
	public String uploadConferenceRooms(MultipartFile file) throws IOException;
	
	public PaginatedResponseDto listAll(String companyId, int page, int size);

}
