package com.letswork.crm.service;

import java.time.LocalDate;
import java.util.List;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.AssignLead;

public interface AssignLeadService {

    AssignLead createAssign(
            AssignLead assignLead
    );

    String bulkAssign(
            String companyId,
            Long userId,
            List<Long> leadIds
    );

    AssignLead getById(
            Long id,
            String companyId
    );

    void delete(
            Long id,
            String companyId
    );

    PaginatedResponseDto getPaginated(
            String companyId,
            Long leadId,
            Long userId,
            String search,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size
    );
}
