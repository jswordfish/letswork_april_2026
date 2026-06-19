package com.letswork.crm.service;

import java.time.LocalDate;
import java.util.List;

import com.letswork.crm.dtos.CommentResponseDto;
import com.letswork.crm.entities.Comment;

public interface CommentService {

    Comment saveOrUpdate(Comment comment);

    Comment getById(Long id, String companyId);

    void delete(Long id, String companyId);

    List<CommentResponseDto> get(
            String companyId,
            Long leadId,
            String comment,
            String search,
            LocalDate fromDate,
            LocalDate toDate
    );
}