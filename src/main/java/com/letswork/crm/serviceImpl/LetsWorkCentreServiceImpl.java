package com.letswork.crm.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.LetsWorkCentreExcelDto;
import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.LetsWorkCentreImage;
import com.letswork.crm.entities.Tenant;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.service.LetsWorkCentreService;
import com.letswork.crm.service.TenantService;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;



@Service
@Transactional
public class LetsWorkCentreServiceImpl implements LetsWorkCentreService {
	
	@Autowired
	LetsWorkCentreRepository repo;
	
	@Autowired
	TenantService tenantService;
	
	@Autowired
    private S3Service s3Service;
	
	private String bucketName = "letsworkcentres";
	
	
	ModelMapper mapper = new ModelMapper();

	@Override
	@Transactional
	public String saveOrUpdate(
	        LetsWorkCentre centre,
	        List<MultipartFile> images,
	        MultipartFile bookTourVideo
	) throws IOException {

	    Tenant tenant = tenantService.findTenantByCompanyId(centre.getCompanyId());
	    if (tenant == null) {
	        throw new RuntimeException("CompanyId invalid - " + centre.getCompanyId());
	    }

	    validateTimeOrder("Weekdays",
	            centre.getStartTimeRegular(),
	            centre.getEndTimeRegular());

	    validateTimeOrder("Saturday",
	            centre.getStartTimeSat(),
	            centre.getEndTimeSat());

	    LetsWorkCentre existing =
	            repo.findByNameAndCompanyIdAndCityAndState(
	                    centre.getName(),
	                    centre.getCompanyId(),
	                    centre.getCity(),
	                    centre.getState()
	            );

	    LetsWorkCentre savedCentre;

	    if (existing != null) {

	        centre.setId(existing.getId());
	        centre.setCreateDate(existing.getCreateDate());
	        centre.setUpdateDate(new Date());

	        if (images != null && !images.isEmpty()) {
	            existing.getImages().clear();
	        }

	        mapper.map(centre, existing);
	        savedCentre = repo.save(existing);

	    } else {

	        centre.setCreateDate(new Date());
	        centre.setUpdateDate(new Date());
	        savedCentre = repo.save(centre);
	    }

	    if (images != null && !images.isEmpty()) {

	        for (MultipartFile mf : images) {

	            File tempFile =
	                    File.createTempFile("centre_", mf.getOriginalFilename());
	            mf.transferTo(tempFile);

	            try {
	                String s3Path =
	                        s3Service.uploadLetsWorkCentreImage(
	                                bucketName,
	                                savedCentre.getCompanyId(),
	                                savedCentre.getName(),
	                                mf.getOriginalFilename(),
	                                tempFile
	                        );

	                LetsWorkCentreImage img = new LetsWorkCentreImage();
	                img.setFileName(mf.getOriginalFilename());
	                img.setS3Path(s3Path);
	                img.setLetsWorkCentre(savedCentre);

	                savedCentre.getImages().add(img);

	            } finally {
	                tempFile.delete();
	            }
	        }

	        repo.save(savedCentre);
	    }

	    if (bookTourVideo != null && !bookTourVideo.isEmpty()) {

	        File tempVideo =
	                File.createTempFile("centre_video_",
	                        bookTourVideo.getOriginalFilename());
	        bookTourVideo.transferTo(tempVideo);

	        try {
	            String videoPath =
	                    s3Service.uploadLetsWorkCentreImage(
	                            bucketName,
	                            savedCentre.getCompanyId(),
	                            savedCentre.getName(),
	                            bookTourVideo.getOriginalFilename(),
	                            tempVideo
	                    );

	            savedCentre.setBookTourVideoPath(videoPath);
	            repo.save(savedCentre);

	        } finally {
	            tempVideo.delete();
	        }
	    }

	    return existing != null ? "record updated" : "record saved";
	}
	
	@Override
    public List<LetsWorkCentreImage> getImagesByCentre(
            String centreName,
            String city,
            String state,
            String companyId
    ) {

        LetsWorkCentre centre =
                repo.findByNameAndCompanyIdAndCityAndState(
                        centreName,
                        companyId,
                        city,
                        state
                );

        if (centre == null) {
            throw new RuntimeException("LetsWorkCentre not found");
        }

        return centre.getImages();
    }

	
	private void validateTimeOrder(String label, LocalTime startTime, LocalTime endTime) {
	    if (startTime == null || endTime == null) {
	        return; 
	    }

	    if (startTime.equals(endTime)) {
	        throw new RuntimeException(label + " timing invalid: start time and end time cannot be the same (" + startTime + ")");
	    }

	    if (startTime.isAfter(endTime)) {
	        throw new RuntimeException(label + " timing invalid: start time (" + startTime + ") cannot be after end time (" + endTime + ")");
	    }
	}
	
	
	private String validate(LetsWorkCentreExcelDto dto) {
		if(dto.getName() == null || dto.getName().length() == 0) {
			return "Name Should not be null";
		}
		
		if(dto.getState() == null || dto.getState().length() == 0) {
			return "State Should not be null";		
			}
		
		if(dto.getCity() == null || dto.getCity().length() == 0) {
			return "City Should not be null";	
			}
		
		if(dto.getCompanyId() == null || dto.getCompanyId().length() == 0) {
			return "CompanyId Should not be null";	
			}
		
		if(dto.getTotalConferenceRooms() == null) {
			return "Total Conference Rooms Should not be null";	
			}
		
		if(dto.getAddress() == null || dto.getAddress().length() == 0) {
			return "Address Should not be null";	
			}
		
		if(dto.getAmenities() == null || dto.getAmenities().length() == 0) {
			return "Amenities Should not be null";	
			}
		
		if(dto.getHasCafe()==null) {
			return "Cafe boolean Should not be null";	
			}
		
		if(dto.getLatitude() == null || dto.getLatitude().length() == 0) {
			return "Latitude Should not be null";	
			}
		
		if(dto.getLongitude() == null || dto.getLongitude().length() == 0) {
			return "Longitude Should not be null";	
			}
		
		if(dto.getCity() == null || dto.getCity().length() == 0) {
			return "City Should not be null";	
			}
		
		if(dto.getState() == null || dto.getState().length() == 0) {
			return "State Should not be null";	
			}
		
		if (dto.getStartTimeRegular() == null) {
	        return "Start Time (Regular) should not be null";
	    }

	    if (dto.getEndTimeRegular() == null) {
	        return "End Time (Regular) should not be null";
	    }

	    if (dto.getStartTimeSat() == null) {
	        return "Start Time (Saturday) should not be null";
	    }

	    if (dto.getEndTimeSat() == null) {
	        return "End Time (Saturday) should not be null";
	    }

	    
	    if (dto.getStartTimeRegular().equals(dto.getEndTimeRegular())) {
	        return "Start and End Time (Regular) cannot be the same";
	    }

	    if (dto.getStartTimeSat().equals(dto.getEndTimeSat())) {
	        return "Start and End Time (Saturday) cannot be the same";
	    }
		
		if(tenantService.findTenantByCompanyId(dto.getCompanyId())==null) {
			return "CompanyId "+dto.getCompanyId()+" does not exists";
		}
		
		
		
		return "ok";
	}
	
	private LocalTime parseTime(String time) {
	    if (time == null || time.trim().isEmpty()) return null;

	    time = time.trim().toUpperCase();
	    DateTimeFormatter formatter12 = DateTimeFormatter.ofPattern("hh:mm a");
	    DateTimeFormatter formatter24 = DateTimeFormatter.ofPattern("HH:mm");

	    try {
	        return LocalTime.parse(time, formatter12);  // Try 12-hour format first
	    } catch (Exception e) {
	        try {
	            return LocalTime.parse(time, formatter24); // Then try 24-hour format
	        } catch (Exception ex) {
	            throw new RuntimeException("Invalid time format: " + time + " (expected formats: 'hh:mm a' or 'HH:mm')");
	        }
	    }
	}
	
	@Override
	public String uploadLetsWorkCentresFromExcel(MultipartFile file) {
        try {
            List<LetsWorkCentreExcelDto> letsWorkCentres = Poiji.fromExcel(file.getInputStream(), PoijiExcelType.XLSX, LetsWorkCentreExcelDto.class);
            
            for(LetsWorkCentreExcelDto dto : letsWorkCentres) {
            	String val = validate(dto);
        		if(!val.equalsIgnoreCase("ok")) {
        			return val;
        		}
        	}
            
//            List<String> responses = letsWorkCentres.stream().map(dto -> {
//            	
//            	LocalTime startRegular = parseTime(dto.getStartTimeRegular());
//                LocalTime endRegular = parseTime(dto.getEndTimeRegular());
//                LocalTime startSat = parseTime(dto.getStartTimeSat());
//                LocalTime endSat = parseTime(dto.getEndTimeSat());
//            	
//            	LetsWorkCentre letsWorkCentre = LetsWorkCentre.builder()
//                        .name(dto.getName().trim())
//                        .totalConferenceRooms(dto.getTotalConferenceRooms())
//                        .address(dto.getAddress().trim())
//                        .companyId(dto.getCompanyId().trim())
//                        .state(dto.getState().trim())
//                        .city(dto.getCity().trim())
//                        .hasCafe(dto.getHasCafe())
//                        .amenities(dto.getAmenities().trim())
//                        .startTimeRegular(startRegular)
//                        .endTimeRegular(endRegular)
//                        .startTimeSat(startSat)
//                        .endTimeSat(endSat)
//                        .build();
//                return saveOrUpdate(letsWorkCentre);
//            }).collect(Collectors.toList());

//            return "Processed " + letsWorkCentres.size() + " LetsWorkCentre successfully.\n"
//                    + String.join("\n", responses);
            return "ok";

        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to process Excel file: " + e.getMessage();
        }
    }

	@Override
	public LetsWorkCentre findByName(String name, String companyId, String city, String state) {
		LetsWorkCentre loc = repo.findByNameAndCompanyIdAndCityAndState(name, companyId, city, state);
		return loc;
	}

	@Override
	public List<LetsWorkCentre> findAll(String companyId) {
		// TODO Auto-generated method stub
		return repo.findAllByCompanyId(companyId);
	}

	@Override
	public String deleteLetsWorkCentre(LetsWorkCentre letsWorkCentre) {
		// TODO Auto-generated method stub
		
		LetsWorkCentre loc = repo.findByNameAndCompanyIdAndCityAndState(letsWorkCentre.getName(), letsWorkCentre.getCompanyId(), letsWorkCentre.getCity(), letsWorkCentre.getState());
		if(loc!=null) {
		repo.delete(loc);
		return "record deleted";
		}
		else return "record not found";
		
	}
	
	private static final int PAGE_SIZE = 10; 

	@Override
	public PaginatedResponseDto getAllLetsWorkCentres(
	        int pageNo,
	        String companyId,
	        String search,
	        String sort
	) {
	    // Default sort
	    Sort sorting = Sort.by("id").descending();

	    // Apply custom sort if provided
	    if (sort != null && !sort.isBlank()) {
	        try {
	            String[] parts = sort.split("=");
	            String field = parts[0];
	            String dir = parts[1];

	            if ("desc".equalsIgnoreCase(dir)) {
	                sorting = Sort.by(field).descending();
	            } else {
	                sorting = Sort.by(field).ascending();
	            }
	        } catch (Exception e) {
	            sorting = Sort.by("id").descending(); // fallback
	        }
	    }

	    Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, sorting);

	    // Normalize empty search
	    if (search != null && search.trim().isEmpty()) {
	        search = null;
	    }

	    Page<LetsWorkCentre> centrePage =
	            repo.search(companyId, search, pageable);

	    return buildPaginatedResponse(centrePage, pageNo);
	}
	
	@Override
    public List<String> getAmenitiesForCentre(String name, String companyId, String city, String state) {
        LetsWorkCentre centre = repo.findByNameAndCompanyIdAndCityAndState(name, companyId, city, state);

        if (centre == null) {
            throw new RuntimeException("Centre not found for the given details");
        }

        String amenities = centre.getAmenities();
        if (amenities == null || amenities.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // Split by comma, trim spaces, and filter out empty strings
        return Arrays.stream(amenities.split(","))
                     .map(String::trim)
                     .filter(s -> !s.isEmpty())
                     .collect(Collectors.toList());
    }
	
	@Override
    public List<String> getAllAmenities(String companyId) {
		List<String> allAmenities = repo.findAllAmenitiesByCompanyId(companyId);

        if (allAmenities.isEmpty()) {
            return Collections.emptyList();
        }

        return allAmenities.stream()
                .filter(Objects::nonNull)
                .flatMap(a -> Arrays.stream(a.split(",")))  
                .map(String::trim)                         
                .filter(s -> !s.isEmpty())                 
                .map(String::toLowerCase)                  
                .distinct()                                
                .sorted()                                  
                .collect(Collectors.toList());
    }

    
    private PaginatedResponseDto buildPaginatedResponse(Page<LetsWorkCentre> letsWorkCentrePage, int page) {
        PaginatedResponseDto response = new PaginatedResponseDto();
        response.setRecordsFrom((page * PAGE_SIZE) + 1);
        response.setRecordsTo(Math.min((page + 1) * PAGE_SIZE, (int) letsWorkCentrePage.getTotalElements()));
        response.setTotalNumberOfRecords((int) letsWorkCentrePage.getTotalElements());
        response.setTotalNumberOfPages(letsWorkCentrePage.getTotalPages());
        response.setSelectedPage(page + 1);
        response.setList(letsWorkCentrePage.getContent());
        return response;
    }

}
