package com.letswork.crm.service;

import java.time.LocalDate;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Comment;

public interface CommentService {

    Comment saveOrUpdate(Comment comment);

    Comment getById(Long id, String companyId);

    void delete(Long id, String companyId);

    PaginatedResponseDto getPaginated(
            String companyId,
            Long leadId,
            String comment,
            String search,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size
    );
}