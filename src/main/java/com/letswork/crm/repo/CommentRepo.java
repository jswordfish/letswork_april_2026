package com.letswork.crm.repo;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.letswork.crm.entities.Comment;

@Repository
public interface CommentRepo extends JpaRepository<Comment, Long> {

    Comment findByIdAndCompanyId(Long id, String companyId);

    @Query(
        "SELECT c FROM Comment c " +
        "WHERE c.companyId = :companyId " +
        "AND (:leadId IS NULL OR c.leadId = :leadId) " +
        "AND (:comment IS NULL OR LOWER(c.comment) LIKE LOWER(CONCAT('%', :comment, '%'))) " +
        "AND (:fromDate IS NULL OR c.createDate >= :fromDate) " +
        "AND (:toDate IS NULL OR c.createDate <= :toDate) " +
        "AND (" +
        ":search IS NULL OR " +
        "LOWER(c.comment) LIKE LOWER(CONCAT('%', :search, '%'))" +
        ")"
    )
    Page<Comment> filter(
            @Param("companyId") String companyId,
            @Param("leadId") Long leadId,
            @Param("comment") String comment,
            @Param("search") String search,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            Pageable pageable
    );
}
