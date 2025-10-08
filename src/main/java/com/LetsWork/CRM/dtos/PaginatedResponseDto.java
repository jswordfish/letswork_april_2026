package com.LetsWork.CRM.dtos;

import java.util.ArrayList;
import java.util.List;

public class PaginatedResponseDto {
	
	Integer recordsFrom;
	
	Integer recordsTo;
	
	Integer totalNumberOfRecords;
	
	Integer totalNumberOfPages;
	
	Integer selectedPage;
	
	//Integer previousPage;
	
	//Integer nextPage;
	
	List<? extends Object> list = new ArrayList<>();
	

	public Integer getRecordsFrom() {
		return recordsFrom;
	}

	public void setRecordsFrom(Integer recordsFrom) {
		this.recordsFrom = recordsFrom;
	}

	public Integer getRecordsTo() {
		return recordsTo;
	}

	public void setRecordsTo(Integer recordsTo) {
		this.recordsTo = recordsTo;
	}

	public Integer getTotalNumberOfRecords() {
		return totalNumberOfRecords;
	}

	public void setTotalNumberOfRecords(Integer totalNumberOfRecords) {
		this.totalNumberOfRecords = totalNumberOfRecords;
	}

	public Integer getTotalNumberOfPages() {
		return totalNumberOfPages;
	}

	public void setTotalNumberOfPages(Integer totalNumberOfPages) {
		this.totalNumberOfPages = totalNumberOfPages;
	}

	public Integer getSelectedPage() {
		return selectedPage;
	}

	public void setSelectedPage(Integer selectedPage) {
		this.selectedPage = selectedPage;
	}

	

	public List<? extends Object> getList() {
		return list;
	}

	public void setList(List<? extends Object> list) {
		this.list = list;
	}

	

}
