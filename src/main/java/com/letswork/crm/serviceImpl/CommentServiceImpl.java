package com.letswork.crm.serviceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.CommentResponseDto;
import com.letswork.crm.entities.Activity;
import com.letswork.crm.entities.Comment;
import com.letswork.crm.entities.Lead;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.entities.User;
import com.letswork.crm.enums.ActionType;
import com.letswork.crm.repo.ActivityRepo;
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
	
	@Autowired
	private ActivityRepo activityRepo;

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

	        Comment savedComment = repo.save(comment);

	        Activity activity = new Activity();
	        activity.setCompanyId(savedComment.getCompanyId());
	        activity.setLeadId(savedComment.getLeadId());
	        activity.setActionType(ActionType.COMMENT_ADDED);
	        activity.setHeader(
	                "comment added on lead - " + lead.getName() + " by " + user.getFirstName() + " " + user.getLastName()
	        );
	        activity.setCreateDate(new Date());
	        activity.setUpdateDate(new Date());

	        activityRepo.save(activity);

	        return savedComment;
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
	
//	@Override
//	public PaginatedResponseDto getPaginated(
//	        String companyId,
//	        Long leadId,
//	        String comment,
//	        String search,
//	        LocalDate fromDate,
//	        LocalDate toDate,
//	        int page,
//	        int size
//	) {
//
//	    Pageable pageable =
//	            PageRequest.of(
//	                    page,
//	                    size,
//	                    Sort.by("createDate").descending());
//
//	    Page<Comment> resultPage =
//	            repo.filter(
//	                    companyId,
//	                    leadId,
//	                    comment,
//	                    search,
//	                    fromDate == null
//	                            ? null
//	                            : java.sql.Date.valueOf(fromDate),
//	                    toDate == null
//	                            ? null
//	                            : java.sql.Date.valueOf(toDate),
//	                    pageable
//	            );
//
//	    PaginatedResponseDto dto =
//	            new PaginatedResponseDto();
//
//	    dto.setSelectedPage(page);
//	    dto.setTotalNumberOfRecords(
//	            (int) resultPage.getTotalElements());
//
//	    dto.setTotalNumberOfPages(
//	            resultPage.getTotalPages());
//
//	    dto.setRecordsFrom(
//	            resultPage.getTotalElements() == 0
//	                    ? 0
//	                    : page * size + 1);
//
//	    dto.setRecordsTo(
//	            Math.min(
//	                    (page + 1) * size,
//	                    (int) resultPage.getTotalElements()));
//
//	    dto.setList(resultPage.getContent());
//
//	    return dto;
//	}
	
	@Override
	public List<CommentResponseDto> get(
	        String companyId,
	        Long leadId,
	        String comment,
	        String search,
	        LocalDate fromDate,
	        LocalDate toDate
	) {

	    List<Comment> comments =
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
	                            : java.sql.Date.valueOf(toDate)
	            );

	    List<CommentResponseDto> response =
	            new ArrayList<>();

	    for (Comment c : comments) {

	        CommentResponseDto dto =
	                new CommentResponseDto();

	        BeanUtils.copyProperties(c, dto);

	        Lead lead =
	                leadRepo.findByIdAndCompanyId(
	                        c.getLeadId(),
	                        companyId);

	        User user =
	                userRepo.findById(
	                        c.getUserId())
	                        .orElse(null);

	        dto.setLead(lead);
	        dto.setUser(user);
	        dto.setCreateDate(c.getCreateDate());

	        response.add(dto);
	    }

	    return response;
	}

}
