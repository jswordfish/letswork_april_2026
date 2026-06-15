package com.letswork.crm.serviceImpl;

import java.time.LocalDate;
import java.util.Date;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Comment;
import com.letswork.crm.entities.Lead;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.entities.User;
import com.letswork.crm.repo.CommentRepo;
import com.letswork.crm.repo.LeadRepo;
import com.letswork.crm.repo.UserRepo;
import com.letswork.crm.service.CommentService;
import com.letswork.crm.service.TenantService;

@Service
@Transactional
public class CommentServiceImpl implements CommentService{
	
	@Autowired
	private CommentRepo repo;

	@Autowired
	private LeadRepo leadRepo;

	@Autowired
	private TenantService tenantService;
	
	@Autowired
	UserRepo userRepo;

	private ModelMapper mapper = new ModelMapper();

	@Override
	public Comment saveOrUpdate(Comment comment) {

	    Tenant tenant =
	            tenantService.findTenantByCompanyId(
	                    comment.getCompanyId());

	    if (tenant == null) {

	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
	                "CompanyId invalid - "
	                        + comment.getCompanyId());
	    }

	    Lead lead =
	            leadRepo.findByIdAndCompanyId(
	                    comment.getLeadId(),
	                    comment.getCompanyId());

	    if (lead == null) {

	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
	                "Lead not found with id : "
	                        + comment.getLeadId());
	    }
	    
	    User user =
	            userRepo.findById(comment.getUserId()).orElse(null);

	    if (user == null) {

	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
	                "User not found with id : "
	                        + comment.getUserId());
	    }

	    if (comment.getId() != null) {

	        Comment existing =
	                repo.findByIdAndCompanyId(
	                        comment.getId(),
	                        comment.getCompanyId());

	        if (existing == null) {

	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
	                    "Comment not found");
	        }

	        comment.setCreateDate(existing.getCreateDate());
	        comment.setUpdateDate(new Date());

	        mapper.map(comment, existing);

	        return repo.save(existing);

	    } else {

	        comment.setCreateDate(new Date());

	        return repo.save(comment);
	    }
	}
	
	@Override
	public Comment getById(Long id, String companyId) {

	    Comment comment =
	            repo.findByIdAndCompanyId(
	                    id,
	                    companyId);

	    if (comment == null) {

	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
	                "Comment not found");
	    }

	    return comment;
	}
	
	@Override
	public void delete(Long id, String companyId) {

	    Comment comment =
	            repo.findByIdAndCompanyId(
	                    id,
	                    companyId);

	    if (comment == null) {

	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
	                "Comment not found");
	    }

	    repo.delete(comment);
	}
	
	@Override
	public PaginatedResponseDto getPaginated(
	        String companyId,
	        Long leadId,
	        String comment,
	        String search,
	        LocalDate fromDate,
	        LocalDate toDate,
	        int page,
	        int size
	) {

	    Pageable pageable =
	            PageRequest.of(
	                    page,
	                    size,
	                    Sort.by("createDate").descending());

	    Page<Comment> resultPage =
	            repo.filter(
	                    companyId,
	                    leadId,
	                    comment,
	                    search,
	                    fromDate == null
	                            ? null
	                            : java.sql.Date.valueOf(fromDate),
	                    toDate == null
	                            ? null
	                            : java.sql.Date.valueOf(toDate),
	                    pageable
	            );

	    PaginatedResponseDto dto =
	            new PaginatedResponseDto();

	    dto.setSelectedPage(page);
	    dto.setTotalNumberOfRecords(
	            (int) resultPage.getTotalElements());

	    dto.setTotalNumberOfPages(
	            resultPage.getTotalPages());

	    dto.setRecordsFrom(
	            resultPage.getTotalElements() == 0
	                    ? 0
	                    : page * size + 1);

	    dto.setRecordsTo(
	            Math.min(
	                    (page + 1) * size,
	                    (int) resultPage.getTotalElements()));

	    dto.setList(resultPage.getContent());

	    return dto;
	}

}
