package com.letswork.crm.service;

import java.time.LocalDate;
import java.util.List;

import com.letswork.crm.entities.BookDayPass;

public interface BookDayPassService {
	
	BookDayPass book(BookDayPass request, String companyId);

    List<BookDayPass> get(
            String companyId,
            String email,
            String letsWorkCentre,
            String city,
            String state,
            LocalDate date
    );

}
