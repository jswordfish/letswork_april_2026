package com.letswork.crm.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letswork.crm.entities.LetsWorkCentre;
import com.letswork.crm.entities.LetsWorkCentreImage;
import com.letswork.crm.repo.LetsWorkCentreRepository;
import com.letswork.crm.service.LetsWorkCentreService;


@RestController
@CrossOrigin
@RequestMapping("/LetsWorkCentre")
public class LetsWorkCentreController {
	
	@Autowired
	LetsWorkCentreService service;
	
	@Autowired
	LetsWorkCentreRepository repo;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@PostMapping(
	        value = "/letswork-centre",
	        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public ResponseEntity<String> createOrUpdate(
	        @RequestPart("centre") String centreJson,
	        @RequestPart(value = "images", required = false)
	                List<MultipartFile> images,
	        @RequestPart(value = "bookTourVideo", required = false)
	                MultipartFile bookTourVideo,
	        @RequestParam String token
	) throws IOException {


	    LetsWorkCentre centre =
	            objectMapper.readValue(centreJson, LetsWorkCentre.class);

	    String result =
	            service.saveOrUpdate(
	                    centre,
	                    images,
	                    bookTourVideo
	            );

	    return ResponseEntity.ok(result);
	}
	
	@GetMapping("/images")
    public ResponseEntity<List<LetsWorkCentreImage>> getCentreImages(
            @RequestParam String centreName,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String companyId,
            @RequestParam String token
    ) {


        return ResponseEntity.ok(
                service.getImagesByCentre(
                        centreName,
                        city,
                        state,
                        companyId
                )
        );
    }
	
	@PostMapping(
		    value = "/upload-excel",
		    consumes = "multipart/form-data"
		)
    public ResponseEntity<String> uploadLetsWorkCentresExcel(@RequestParam("file") MultipartFile file, @RequestParam String token) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a valid Excel file.");
        }

        String response = service.uploadLetsWorkCentresFromExcel(file);
        return ResponseEntity.ok(response);
    }
	
//	@GetMapping("/fetch all LetsWorkCentres")
//	public List<LetsWorkCentre> fetchAll(@RequestParam String token, @RequestParam String companyId){
//		return service.findAll(companyId);
//	}
	
	@GetMapping
	public ResponseEntity<?> getAllLetsWorkCentres(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam String token,
	        @RequestParam String companyId,
	        @RequestParam(required = false) String search,
	        @RequestParam(required = false) String sort,
	        @RequestParam(required = false) String letsWorkCentre,
	        @RequestParam(required = false) String city,
	        @RequestParam(required = false) String state
	) {
		
		if((letsWorkCentre!=null)&&(city!=null)&&(state!=null)) {
			return ResponseEntity.ok(repo.findByNameAndCompanyIdAndCityAndState(letsWorkCentre, companyId, city, state));
		}
		
		
	    return ResponseEntity.ok(
	            service.getAllLetsWorkCentres(page, companyId, search, sort)
	    );
	}
	
//	@GetMapping("/fetch-amenities")
//    public ResponseEntity<List<String>> getAmenitiesForCentre(
//            @RequestParam String name,
//            @RequestParam String companyId,
//            @RequestParam String city,
//            @RequestParam String state,
//            @RequestParam String token) {
//
//        List<String> amenities = service.getAmenitiesForCentre(name, companyId, city, state);
//        return ResponseEntity.ok(amenities);
//    }
	
	@GetMapping("/amenities")
	public ResponseEntity<List<String>> getAllAmenitiesByCompany(
            @RequestParam String companyId,
            @RequestParam String token) {

        List<String> amenities = service.getAllAmenities(companyId);
        return ResponseEntity.ok(amenities);
    }
	
	@DeleteMapping
	public String deleteLetsWorkCentre(@RequestBody LetsWorkCentre letsWorkCentre, @RequestParam String token) {
		
		return service.deleteLetsWorkCentre(letsWorkCentre);
		
	}
	

}
