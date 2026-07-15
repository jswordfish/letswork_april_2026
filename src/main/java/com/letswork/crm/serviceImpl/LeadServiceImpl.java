package com.letswork.crm.serviceImpl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.LeadResponseDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Activity;
import com.letswork.crm.entities.AssignLead;
import com.letswork.crm.entities.Lead;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.Solutions;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.entities.User;
import com.letswork.crm.enums.ActionType;
import com.letswork.crm.enums.LeadQuality;
import com.letswork.crm.enums.LeadStatus;
import com.letswork.crm.enums.Source;
import com.letswork.crm.repo.ActivityRepo;
import com.letswork.crm.repo.AssignLeadRepo;
import com.letswork.crm.repo.LeadRepo;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.repo.SolutionsRepository;
import com.letswork.crm.repo.UserRepo;
import com.letswork.crm.service.LeadService;
import com.letswork.crm.service.TenantService;

@Service
@Transactional
public class LeadServiceImpl implements LeadService{
	
	@Autowired
	LeadRepo repo;
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
	LetsWorkCentreRepository letsWorkCentreRepo;
	
	@Autowired
	SolutionsRepository solutionRepo;
	
	@Autowired
	private ActivityRepo activityRepo;
	
	@Autowired
	AssignLeadRepo assignLeadRepo;
	
	@Autowired
	UserRepo userRepo;
	
	ModelMapper mapper = new ModelMapper();


	@Override
	public Lead saveOrUpdate(Lead lead) {

	    Tenant tenant = tenantService.findTenantByCompanyId(lead.getCompanyId());

	    if (tenant == null) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
	                "CompanyId invalid - " + lead.getCompanyId());
	    }
	    
		LetsWorkCentre centre = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(lead.getLetsWorkCentre(), lead.getCompanyId(), lead.getCity(), lead.getState());
		
		if(centre==null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This LetsWorkCentre does not exists");
		}
		
		Solutions solution = solutionRepo.findByNameAndLetsWorkCentreAndCompanyId(lead.getSolution(), lead.getLetsWorkCentre(), lead.getCompanyId());
		
		if(solution==null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This Solution does not exists");
		}
		

	    if (lead.getId() != null) {

	        Lead existing =
	                repo.findByIdAndCompanyId(
	                        lead.getId(),
	                        lead.getCompanyId());

	        if (existing == null) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lead not found");
	        }

	        if (!existing.getEmail().equalsIgnoreCase(lead.getEmail())) {

	            Lead emailLead =
	                    repo.findByEmailAndCompanyId(
	                            lead.getEmail(),
	                            lead.getCompanyId());

	            if (emailLead != null) {
	                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
	                        "Lead already exists with email : "
	                                + lead.getEmail());
	            }
	        }

	        if (!existing.getPhone().equals(lead.getPhone())) {

	            Lead phoneLead =
	                    repo.findByPhoneAndCompanyId(
	                            lead.getPhone(),
	                            lead.getCompanyId());

	            if (phoneLead != null) {
	                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
	                        "Lead already exists with phone : "
	                                + lead.getPhone());
	            }
	        }

	        lead.setCreateDate(existing.getCreateDate());
	        lead.setUpdateDate(new Date());

	        mapper.map(lead, existing);

	        return repo.save(existing);

	    } else {

	        Lead emailLead =
	                repo.findByEmailAndCompanyId(
	                        lead.getEmail(),
	                        lead.getCompanyId());

	        if (emailLead != null) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
	                    "Lead already exists with email : "
	                            + lead.getEmail());
	        }

	        Lead phoneLead =
	                repo.findByPhoneAndCompanyId(
	                        lead.getPhone(),
	                        lead.getCompanyId());

	        if (phoneLead != null) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
	                    "Lead already exists with phone : "
	                            + lead.getPhone());
	        }

	        lead.setCreateDate(new Date());

	        Lead savedLead = repo.save(lead);

	        Activity activity = new Activity();
	        activity.setCompanyId(savedLead.getCompanyId());
	        activity.setLeadId(savedLead.getId());
	        activity.setActionType(ActionType.LEAD_CREATED);
	        activity.setHeader(
	                "Lead created at " + new Date()
	        );
	        activity.setCreateDate(new Date());
	        activity.setUpdateDate(new Date());

	        activityRepo.save(activity);

	        return savedLead;
	    }
	}

	@Override
	public PaginatedResponseDto getPaginated(
	        String companyId,
	        String name,
	        String email,
	        String phone,
	        String clientCompanyName,
	        List<Source> sources,
	        String location,
	        List<LeadStatus> statuses,
	        List<LeadQuality> leadQualities,
	        List<String> letsWorkCentres,
	        String city,
	        String state,
	        String solution,
	        String search,
	        Long userId,
	        LocalDate fromDate,
	        LocalDate toDate,
	        int page,
	        int size
	) {

	    Pageable pageable =
	            PageRequest.of(
	                    page,
	                    size,
	                    Sort.by("createDate").descending()
	            );
	    
	    boolean checkSources = sources != null && !sources.isEmpty();
	    boolean checkStatuses = statuses != null && !statuses.isEmpty();
	    boolean checkLeadQualities = leadQualities != null && !leadQualities.isEmpty();
	    boolean checkLetsWorkCentres = letsWorkCentres != null && !letsWorkCentres.isEmpty();

	    Page<Lead> resultPage =
	            repo.filter(
	                    companyId,
	                    userId,
	                    name,
	                    email,
	                    phone,
	                    clientCompanyName,
	                    checkSources,
	                    sources,
	                    location,
	                    checkStatuses,
	                    statuses,
	                    checkLeadQualities,
	                    leadQualities,
	                    checkLetsWorkCentres,
	                    letsWorkCentres,
	                    city,
	                    state,
	                    solution,
	                    search,
	                    fromDate == null ? null : java.sql.Date.valueOf(fromDate),
	                    toDate == null ? null : java.sql.Date.valueOf(toDate),
	                    pageable
	            );

	    List<Lead> leads = resultPage.getContent();

	    Set<Long> leadIds =
	            leads.stream()
	                    .map(Lead::getId)
	                    .collect(Collectors.toSet());

	    List<AssignLead> assignments =
	            leadIds.isEmpty()
	                    ? Collections.emptyList()
	                    : assignLeadRepo.findByLeadIdIn(leadIds);

	    Map<Long, AssignLead> assignmentMap =
	            assignments.stream()
	                    .collect(
	                            Collectors.toMap(
	                                    AssignLead::getLeadId,
	                                    Function.identity()
	                            )
	                    );

	    Set<Long> userIds =
	            assignments.stream()
	                    .map(AssignLead::getUserId)
	                    .collect(Collectors.toSet());

	    Map<Long, User> userMap =
	            userIds.isEmpty()
	                    ? Collections.emptyMap()
	                    : userRepo.findByIdIn(userIds)
	                            .stream()
	                            .collect(
	                                    Collectors.toMap(
	                                            User::getId,
	                                            Function.identity()
	                                    )
	                            );

	    List<LeadResponseDto> response =
	            new ArrayList<>();

	    for (Lead lead : leads) {

	        LeadResponseDto dto =
	                new LeadResponseDto();

	        BeanUtils.copyProperties(
	                lead,
	                dto
	        );

	        AssignLead assignment =
	                assignmentMap.get(
	                        lead.getId()
	                );

	        if (assignment != null) {

	            dto.setUser(
	                    userMap.get(
	                            assignment.getUserId()
	                    )
	            );
	        }

	        response.add(dto);
	    }

	    PaginatedResponseDto dto =
	            new PaginatedResponseDto();

	    dto.setSelectedPage(page);

	    dto.setTotalNumberOfRecords(
	            (int) resultPage.getTotalElements()
	    );

	    dto.setTotalNumberOfPages(
	            resultPage.getTotalPages()
	    );

	    dto.setRecordsFrom(
	            resultPage.getTotalElements() == 0
	                    ? 0
	                    : page * size + 1
	    );

	    dto.setRecordsTo(
	            Math.min(
	                    (page + 1) * size,
	                    (int) resultPage.getTotalElements()
	            )
	    );

	    dto.setList(response);

	    return dto;
	}
	
	@Override
	public Lead changeStatus(
	        Long leadId,
	        String companyId,
	        LeadStatus status
	) {

	    Tenant tenant =
	            tenantService.findTenantByCompanyId(
	                    companyId
	            );

	    if (tenant == null) {

	        throw new RuntimeException(
	                "CompanyId invalid - "
	                        + companyId
	        );
	    }

	    Lead lead =
	            repo.findByIdAndCompanyId(
	                    leadId,
	                    companyId
	            );

	    if (lead == null) {

	        throw new RuntimeException(
	                "Lead not found"
	        );
	    }
	    
	    LeadStatus oldStatus = lead.getStatus();

	    lead.setStatus(status);
	    lead.setUpdateDate(new Date());

	    Lead savedLead = repo.save(lead);

        Activity activity = new Activity();
        activity.setCompanyId(savedLead.getCompanyId());
        activity.setLeadId(savedLead.getId());
        activity.setActionType(ActionType.STATUS_CHANGED);
        activity.setHeader(
                "Status changed of lead - " + savedLead.getName() + " from " + oldStatus + " to " + status
        );
        activity.setCreateDate(new Date());
        activity.setUpdateDate(new Date());

        activityRepo.save(activity);

        return savedLead;
	}

	@Override
	public void delete(Long id, String companyId) {

	    Lead lead =
	            repo.findByIdAndCompanyId(id, companyId);

	    if (lead == null) {
	        throw new RuntimeException("Lead not found");
	    }

	    repo.delete(lead);
	}

	@Override
	public LeadResponseDto getById(Long id, String companyId) {

	    Lead lead =
	            repo.findByIdAndCompanyId(id, companyId);

	    if (lead == null) {
	        throw new RuntimeException("Lead not found");
	    }

	    LeadResponseDto dto =
	            new LeadResponseDto();

	    BeanUtils.copyProperties(
	            lead,
	            dto
	    );

	    AssignLead assignment =
	            assignLeadRepo.findByLeadId(
	                    lead.getId()
	            );

	    if (assignment != null) {

	        User user =
	                userRepo.findById(
	                        assignment.getUserId()
	                ).orElse(null);

	        dto.setUser(user);
	    }

	    return dto;
	}
	
	@Override
	public void exportToExcel(
	        String companyId,
	        String name,
	        String email,
	        String phone,
	        String clientCompanyName,
	        List<Source> sources,
	        String location,
	        List<LeadStatus> statuses,
	        List<LeadQuality> leadQualities,
	        List<String> letsWorkCentres,
	        String city,
	        String state,
	        String solution,
	        String search,
	        Long userId,
	        LocalDate fromDate,
	        LocalDate toDate,
	        HttpServletResponse response
	) throws IOException {
	    
	    Page<Lead> resultPage;
	    
	    boolean checkSources = sources != null && !sources.isEmpty();
	    boolean checkStatuses = statuses != null && !statuses.isEmpty();
	    boolean checkLeadQualities = leadQualities != null && !leadQualities.isEmpty();
	    boolean checkLetsWorkCentres = letsWorkCentres != null && !letsWorkCentres.isEmpty();

	    // Create a sort definition to bring records in descending order by ID
	    Sort sortOrder = Sort.by(Sort.Direction.DESC, "id");

	    resultPage = repo.filter(
	            companyId,
	            userId,
	            name,
	            email,
	            phone,
	            clientCompanyName,
	            checkSources,
	            sources,
	            location,
	            checkStatuses,
	            statuses,
	            checkLeadQualities,
	            leadQualities,
	            checkLetsWorkCentres,
	            letsWorkCentres,
	            city,
	            state,
	            solution,
	            search,
	            fromDate == null ? null : java.sql.Date.valueOf(fromDate),
	            toDate == null ? null : java.sql.Date.valueOf(toDate),
	            PageRequest.of(0, Integer.MAX_VALUE, sortOrder) // Requests all records sorted DESC by ID
	    );

	    List<Lead> leads = resultPage.getContent();
	    List<LeadResponseDto> dtos = buildResponseList(leads);

	    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	    response.setHeader("Content-Disposition", "attachment; filename=leads_export.xlsx");

	    String[] headers = {
	            "Name", "Email", "Phone Number", "Company Name",
	            "Source", "Location", "Status", "Lead Quality",
	            "Lets Work Centre", "City", "State", "Solution",
	            "Assigned User", "Create Date", "Number Of Seats"
	    };

	    try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) {
	        SXSSFSheet sheet = workbook.createSheet("Leads");
	        sheet.trackAllColumnsForAutoSizing();

	        CellStyle headerStyle = workbook.createCellStyle();
	        Font headerFont = workbook.createFont();
	        headerFont.setBold(true);
	        headerStyle.setFont(headerFont);

	        Row headerRow = sheet.createRow(0);

	        for (int i = 0; i < headers.length; i++) {
	            Cell cell = headerRow.createCell(i);
	            cell.setCellValue(headers[i]);
	            cell.setCellStyle(headerStyle);
	        }

	        int rowIdx = 1;

	        for (LeadResponseDto dto : dtos) {
	            Row row = sheet.createRow(rowIdx++);

//	            row.createCell(0).setCellValue(dto.getId() != null ? dto.getId() : 0);
	            row.createCell(0).setCellValue(nullSafe(dto.getName()));
	            row.createCell(1).setCellValue(nullSafe(dto.getEmail()));
	            row.createCell(2).setCellValue(nullSafe(dto.getPhone()));
	            row.createCell(3).setCellValue(nullSafe(dto.getClientCompanyName()));
	            row.createCell(4).setCellValue(dto.getSource() != null ? dto.getSource().toString() : "");
	            row.createCell(5).setCellValue(nullSafe(dto.getLocation()));
	            row.createCell(6).setCellValue(dto.getStatus() != null ? dto.getStatus().toString() : "");
	            row.createCell(7).setCellValue(dto.getLeadQuality() != null ? dto.getLeadQuality().toString() : "");
	            row.createCell(8).setCellValue(nullSafe(dto.getLetsWorkCentre()));
	            row.createCell(9).setCellValue(nullSafe(dto.getCity()));
	            row.createCell(10).setCellValue(nullSafe(dto.getState()));
	            row.createCell(11).setCellValue(nullSafe(dto.getSolution()));
	            row.createCell(12).setCellValue(dto.getUser() != null ? dto.getUser().getFirstName() : "");
	            row.createCell(13).setCellValue(dto.getCreateDate() != null ? dto.getCreateDate().toString() : "");
	            row.createCell(14).setCellValue(dto.getNumberOfSeats() != null ? dto.getNumberOfSeats() : 0);
	        }

	        for (int i = 0; i < headers.length; i++) {
	            sheet.autoSizeColumn(i);
	        }

	        workbook.write(response.getOutputStream());
	        response.flushBuffer();
	        workbook.dispose();
	    }
	}

	private String nullSafe(String value) {
	    return value == null ? "" : value;
	}
	
	private List<LeadResponseDto> buildResponseList(List<Lead> leads) {

	    Set<Long> leadIds =
	            leads.stream()
	                    .map(Lead::getId)
	                    .collect(Collectors.toSet());

	    List<AssignLead> assignments =
	            leadIds.isEmpty()
	                    ? Collections.emptyList()
	                    : assignLeadRepo.findByLeadIdIn(leadIds);

	    Map<Long, AssignLead> assignmentMap =
	            assignments.stream()
	                    .collect(Collectors.toMap(AssignLead::getLeadId, Function.identity()));

	    Set<Long> userIds =
	            assignments.stream()
	                    .map(AssignLead::getUserId)
	                    .collect(Collectors.toSet());

	    Map<Long, User> userMap =
	            userIds.isEmpty()
	                    ? Collections.emptyMap()
	                    : userRepo.findByIdIn(userIds)
	                            .stream()
	                            .collect(Collectors.toMap(User::getId, Function.identity()));

	    List<LeadResponseDto> response = new ArrayList<>();

	    for (Lead lead : leads) {
	        LeadResponseDto dto = new LeadResponseDto();
	        BeanUtils.copyProperties(lead, dto);

	        AssignLead assignment = assignmentMap.get(lead.getId());
	        if (assignment != null) {
	            dto.setUser(userMap.get(assignment.getUserId()));
	        }
	        response.add(dto);
	    }

	    return response;
	}

}
