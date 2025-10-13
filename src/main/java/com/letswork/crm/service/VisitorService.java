package com.LetsWork.CRM.service;

import java.time.LocalDate;
import java.util.List;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.Visitor;


public interface VisitorService {
	
	String saveOrUpdate(Visitor visitor);
	
	List<Visitor> viewByDate(LocalDate visitDate);
	
	PaginatedResponseDto viewByDate(LocalDate visitDate, String companyId, int page);
	
	String deleteVisitor(Visitor visitor);

}
