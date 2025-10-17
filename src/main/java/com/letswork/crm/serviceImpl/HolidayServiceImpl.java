package com.letswork.crm.serviceImpl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.HolidayExcelDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Holiday;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.HolidayRepository;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.service.HolidayService;
import com.letswork.crm.service.TenantService;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;

@Service
@Transactional
public class HolidayServiceImpl implements HolidayService {

    @Autowired
    private HolidayRepository holidayRepo;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private LetsWorkCentreRepository letsWorkCentreRepo;

    ModelMapper mapper = new ModelMapper();
    
    DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");

    @Override
    public String saveOrUpdate(Holiday holiday) {
        Tenant tenant = tenantService.findTenantByCompanyId(holiday.getCompanyId());
        if (tenant == null) {
            throw new RuntimeException("CompanyId invalid - " + holiday.getCompanyId());
        }

        LetsWorkCentre centre = letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(
                holiday.getLetsWorkCentre(), holiday.getCompanyId(), holiday.getCity(), holiday.getState());

        if (centre == null) {
            throw new RuntimeException("This LetsWorkCentre does not exist");
        }

        Holiday existing = holidayRepo.findByLetsWorkCentreAndHolidayDateAndCityAndStateAndCompanyId(
                holiday.getLetsWorkCentre(), holiday.getHolidayDate(), holiday.getCity(), holiday.getState(), holiday.getCompanyId());

        if (existing != null) {
            holiday.setId(existing.getId());
            holiday.setCreateDate(existing.getCreateDate());
            holiday.setUpdateDate(new Date());
            mapper.map(holiday, existing);
            holidayRepo.save(existing);
            return "record updated";
        } else {
            holiday.setCreateDate(new Date());
            holidayRepo.save(holiday);
            return "record saved";
        }
    }

    @Override
    public PaginatedResponseDto listHolidays(String companyId, String letsWorkCentre, String city, String state, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("holidayDate").descending());
        Page<Holiday> pagedResult = holidayRepo.findByFilters(companyId, letsWorkCentre, city, state, pageable);

        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom(page * size + 1);
        response.setRecordsTo(page * size + pagedResult.getNumberOfElements());
        response.setTotalNumberOfRecords((int) pagedResult.getTotalElements());
        response.setTotalNumberOfPages(pagedResult.getTotalPages());
        response.setSelectedPage(page + 1);
        response.setList(pagedResult.getContent());
        return response;
    }

    private String validate(HolidayExcelDto dto) {
        if (dto.getLetsWorkCentre() == null || dto.getLetsWorkCentre().isEmpty())
            return "LetsWorkCentre should not be null or empty";

        if (dto.getCompanyId() == null || dto.getCompanyId().isEmpty())
            return "CompanyId should not be null or empty";

        if (dto.getCity() == null || dto.getCity().isEmpty())
            return "City should not be null or empty";

        if (dto.getState() == null || dto.getState().isEmpty())
            return "State should not be null or empty";

        if (dto.getHolidayDate() == null)
            return "Holiday Date should not be null";

        if (dto.getHolidayReason() == null || dto.getHolidayReason().isEmpty())
            return "Holiday Reason should not be null or empty";

        if (tenantService.findTenantByCompanyId(dto.getCompanyId()) == null)
            return "CompanyId " + dto.getCompanyId() + " does not exist";

        if (letsWorkCentreRepo.findByNameAndCompanyIdAndCityAndState(
                dto.getLetsWorkCentre(), dto.getCompanyId(), dto.getCity(), dto.getState()) == null)
            return "LetsWorkCentre " + dto.getLetsWorkCentre() + " does not exist";

        return "ok";
    }

    @Override
    public String uploadHolidays(MultipartFile file) throws IOException {
        List<HolidayExcelDto> dtos = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, HolidayExcelDto.class);

        for (HolidayExcelDto dto : dtos) {
            String val = validate(dto);
            if (!val.equalsIgnoreCase("ok")) {
                return val;
            }
        }

        List<String> responses = new ArrayList<>();

        for (HolidayExcelDto dto : dtos) {
            try {
                Holiday holiday = new Holiday();
                holiday.setLetsWorkCentre(dto.getLetsWorkCentre().trim());
                holiday.setCompanyId(dto.getCompanyId().trim());
                holiday.setCity(dto.getCity().trim());
                holiday.setState(dto.getState().trim());
                Date dt = dateFormat.parse(dto.getHolidayDate());
                holiday.setHolidayDate(dt);
                holiday.setHolidayReason(dto.getHolidayReason().trim());

                String result = saveOrUpdate(holiday);
                responses.add(result + " : " + dto.getHolidayDate());
            } catch (Exception e) {
                responses.add("Error saving " + dto.getHolidayDate() + ": " + e.getMessage());
            }
        }

        return "ok";
    }
}
