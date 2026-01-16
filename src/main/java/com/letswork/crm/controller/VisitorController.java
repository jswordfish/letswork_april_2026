package com.letswork.crm.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Visitor;
import com.letswork.crm.repo.VisitorRepository;
import com.letswork.crm.service.VisitorService;


@RestController
@CrossOrigin
@RequestMapping("/visitor")
public class VisitorController {
	
	@Autowired
	VisitorService service;
	
	@Autowired
	VisitorRepository repo;
	
	@PostMapping
	public ResponseEntity<String> saveOrUpdate(
	        @RequestBody Visitor visitor,
	        @RequestParam String token
	) {
	    return ResponseEntity.ok(
	            service.saveOrUpdate(visitor)
	    );
	}
	
//	@GetMapping("/view visitor by date")
//	public List<Visitor> viewByDate(@RequestParam LocalDate visitDate, @RequestParam String token){
//		
//		return service.viewByDate(visitDate);
//		
//	}
	
	@GetMapping("/scan")
	public ResponseEntity<Visitor> scan(
            @RequestParam String qrData,
            @RequestParam String token
    ) {
       
        String bookingCode = qrData.split("\\|")[1];

        Visitor visitor = repo.findByBookingCode(bookingCode);
        
        if(visitor==null) {
        	throw new RuntimeException("Visitor not found");
        }

        return ResponseEntity.ok(visitor);
        
    }
	
	@PostMapping("/allow")
	public ResponseEntity<Visitor> allow(
	        @RequestBody Visitor request,
	        @RequestParam String token
	) {

	    if (Boolean.TRUE.equals(request.getVisited())) {
	        throw new ResponseStatusException(
	                HttpStatus.CONFLICT,
	                "Visitor entry already used"
	        );
	    }

	    LocalDate today = LocalDate.now();

	    if (!today.equals(request.getVisitDate())) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST,
	                "Visitor can only visit on the booking date"
	        );
	    }

	    request.setVisited(true);
	    repo.save(request);

	    return ResponseEntity.ok(request);
	}
	
	
	@GetMapping
	public ResponseEntity<PaginatedResponseDto> filterVisitors(
	        @RequestParam String companyId,
	        @RequestParam(required = false) String name,
	        @RequestParam(required = false) String email,
	        @RequestParam(required = false) String emailOfVisitor,
	        @RequestParam(required = false)
	        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	        LocalDate visitDate,
	        @RequestParam(required = false) String letsWorkCentre,
	        @RequestParam(required = false) String city,
	        @RequestParam(required = false) String state,
	        @RequestParam(required = false) String type,
	        @RequestParam String token,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size
	) {

	    return ResponseEntity.ok(
	            service.filterPaginated(
	                    companyId,
	                    name,
	                    email,
	                    emailOfVisitor,
	                    visitDate,
	                    letsWorkCentre,
	                    city,
	                    state,
	                    type,
	                    page,
	                    size
	            )
	    );
	}
	
	@GetMapping("/date")
    public ResponseEntity<PaginatedResponseDto> viewByDate(
            @RequestParam LocalDate visitDate,
            @RequestParam String companyId,
            @RequestParam String token,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(service.viewByDate(visitDate, companyId, page));
    }
	
	@DeleteMapping
	public String deleteVisitor(@RequestBody Visitor visitor, @RequestParam String token) {
		
		return service.deleteVisitor(visitor);
		
	}

}
