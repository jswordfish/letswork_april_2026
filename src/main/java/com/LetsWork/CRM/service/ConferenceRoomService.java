package com.LetsWork.CRM.service;

import java.util.List;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.ConferenceRoom;


public interface ConferenceRoomService {
	
	String saveOrUpdate(ConferenceRoom conferenceRoom);
	
	List<ConferenceRoom> findByLocation(String location, String companyId);
	
	PaginatedResponseDto findByLocation(String location, String companyId, int page);
	
//	PaginatedResponseDto findAvailableConferenceRooms(Boolean available, int page);
	
	String deleteByName(ConferenceRoom conferenceRoom);

}
