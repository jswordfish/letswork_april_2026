package com.letswork.crm.service;

import java.time.LocalDate;
import java.util.List;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Visitor;


public interface VisitorService {
	
	String saveOrUpdate(Visitor visitor);
	
	List<Visitor> viewByDate(LocalDate visitDate);
	
	PaginatedResponseDto viewByDate(LocalDate visitDate, String companyId, int page);
	
	String deleteVisitor(Visitor visitor);
	
	List<Visitor> filter(
	        String companyId,
	        String name,
	        String email,
	        String emailOfVisitor,
	        LocalDate visitDate,
	        String centre,
	        String city,
	        String state,
	        String type
	);

}
