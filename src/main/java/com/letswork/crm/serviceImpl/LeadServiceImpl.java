package com.letswork.crm.serviceImpl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
import org.springframework.web.multipart.MultipartFile;
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
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;

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
	                "Lead created at " + getFormattedCurrentDateTime()
	        );
	        activity.setCreateDate(new Date());
	        activity.setUpdateDate(new Date());

	        activityRepo.save(activity);

	        return savedLead;
	    }
	}
	
	private String getFormattedCurrentDateTime() {
	    LocalDateTime now = LocalDateTime.now();
	    int day = now.getDayOfMonth();
	    
	    // 1. Determine the ordinal suffix (st, nd, rd, th)
	    String suffix;
	    if (day >= 11 && day <= 13) {
	        suffix = "th";
	    } else {
	        switch (day % 10) {
	            case 1:  suffix = "st"; break;
	            case 2:  suffix = "nd"; break;
	            case 3:  suffix = "rd"; break;
	            default: suffix = "th"; break;
	        }
	    }
	    
	    // 2. Format the rest of the date: "July 2026 - 03:45 PM"
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy - hh:mm a");
	    String restOfDate = now.format(formatter);
	    
	    // 3. Combine them: "16" + "th " + "July 2026 - 03:45 PM"
	    return day + suffix + " " + restOfDate;
	}
	
	@Override
	public String uploadLeadsFromExcel(
	        MultipartFile file,
	        String companyId
	) throws IOException {

	    List<Lead> leads =
	            Poiji.fromExcel(
	                    file.getInputStream(),
	                    PoijiExcelType.XLSX,
	                    Lead.class
	            );

	    for (Lead lead : leads) {

	        lead.setName(trim(lead.getName()));
	        lead.setEmail(trim(lead.getEmail()));
	        lead.setPhone(trim(lead.getPhone()));
	        lead.setClientCompanyName(trim(lead.getClientCompanyName()));
	        lead.setLocation(trim(lead.getLocation()));
	        lead.setLetsWorkCentre(trim(lead.getLetsWorkCentre()));
	        lead.setCity(trim(lead.getCity()));
	        lead.setState(trim(lead.getState()));
	        lead.setSolution(trim(lead.getSolution()));

	        String validation = validate(lead);

	        if (!validation.equalsIgnoreCase("ok")) {
	            return "Validation failed: " + validation;
	        }
	    }

	    List<String> errors = new ArrayList<>();

	    for (Lead lead : leads) {

	        try {

	            lead.setCompanyId(companyId);

	            saveOrUpdate(lead);

	        } catch (Exception e) {

	            errors.add(
	                    "Error importing lead "
	                            + (lead.getEmail() != null ? lead.getEmail() : lead.getPhone())
	                            + " : "
	                            + e.getMessage()
	            );
	        }
	    }

	    if (!errors.isEmpty()) {
	        return "UPLOAD PARTIALLY FAILED:\n" + String.join("\n", errors);
	    }

	    return "ok";
	}
	
	private String validate(Lead dto) {

	    if (dto.getName() == null || dto.getName().trim().isEmpty()) {
	        return "Name should not be null";
	    }

	    if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
	        return "Email should not be null";
	    }

	    if (dto.getPhone() == null || dto.getPhone().trim().isEmpty()) {
	        return "Phone should not be null";
	    }

	    if (dto.getSource() == null) {
	        return "Source should not be null";
	    }

	    if (dto.getStatus() == null) {
	        return "Status should not be null";
	    }

	    if (dto.getLeadQuality() == null) {
	        return "Lead Quality should not be null";
	    }

	    if (dto.getLetsWorkCentre() == null || dto.getLetsWorkCentre().trim().isEmpty()) {
	        return "LetsWorkCentre should not be null";
	    }

	    if (dto.getCity() == null || dto.getCity().trim().isEmpty()) {
	        return "City should not be null";
	    }

	    if (dto.getState() == null || dto.getState().trim().isEmpty()) {
	        return "State should not be null";
	    }

	    if (dto.getSolution() == null || dto.getSolution().trim().isEmpty()) {
	        return "Solution should not be null";
	    }

	    return "ok";
	}
	
	private String trim(String value) {
	    return value == null ? null : value.trim();
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
	            "Lets Work Centre", "Solution",
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
	            row.createCell(9).setCellValue(nullSafe(dto.getSolution()));
	            row.createCell(10).setCellValue(dto.getUser() != null ? dto.getUser().getFirstName() : "");
	            row.createCell(11).setCellValue(formatDateWithSuffix(dto.getCreateDate()));
	            row.createCell(12).setCellValue(dto.getNumberOfSeats() != null ? dto.getNumberOfSeats() : 0);
	        }

	        for (int i = 0; i < headers.length; i++) {
	            sheet.autoSizeColumn(i);
	        }

	        workbook.write(response.getOutputStream());
	        response.flushBuffer();
	        workbook.dispose();
	    }
	}
	
	public static String formatDateWithSuffix(Date date) {
	    if (date == null) {
	        return "";
	    }

	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    int day = cal.get(Calendar.DAY_OF_MONTH);

	    // Determine day-of-month ordinal suffix (st, nd, rd, th)
	    String suffix;
	    if (day >= 11 && day <= 13) {
	        suffix = "th";
	    } else {
	        switch (day % 10) {
	            case 1:  suffix = "st"; break;
	            case 2:  suffix = "nd"; break;
	            case 3:  suffix = "rd"; break;
	            default: suffix = "th"; break;
	        }
	    }

	    // Pattern format: "Mon, 4" + "th" + " July, 2026"
	    SimpleDateFormat prefixFormat = new SimpleDateFormat("EEE, d", Locale.ENGLISH);
	    SimpleDateFormat suffixFormat = new SimpleDateFormat(" MMMM, yyyy", Locale.ENGLISH);

	    return prefixFormat.format(date) + suffix + suffixFormat.format(date);
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
