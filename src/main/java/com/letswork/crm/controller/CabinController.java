package com.letswork.crm.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.Cabin;
import com.letswork.crm.service.CabinService;

@RestController
@RequestMapping("/api/cabin")
@CrossOrigin
public class CabinController {

    @Autowired
    private CabinService cabinService;

    @PostMapping("/saveOrUpdate")
    public Cabin saveOrUpdate(@RequestBody Cabin cabin, @RequestParam String token) {
        return cabinService.saveOrUpdate(cabin);
    }

    @GetMapping("/list")
    public PaginatedResponseDto listAll(@RequestParam String companyId,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam String token) {
        return cabinService.listAll(companyId, page, size);
    }

    @DeleteMapping("/delete")
    public String delete(@RequestParam Long id, @RequestParam String token) {
        cabinService.delete(id);
        return "Cabin deleted successfully";
    }

    @PostMapping("/upload")
    public List<String> uploadCabins(@RequestParam("file") MultipartFile file,
    		@RequestParam String token) throws IOException {
        return cabinService.uploadCabins(file);
    }
}
