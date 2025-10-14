package com.letswork.crm.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.dtos.PaginatedResponseDto;
import com.letswork.crm.entities.WifiRouter;
import com.letswork.crm.service.WifiRouterService;


@RestController
@CrossOrigin
public class WifiRouterController {
	
	@Autowired
    private WifiRouterService service;

    @PostMapping("/create or update wifi router")
    public String saveOrUpdate(@RequestBody WifiRouter wifiRouter, @RequestParam String token) {
    	
        return service.saveOrUpdate(wifiRouter);
    }
    
    @PostMapping(value = "/wifi-routers-upload-excel", consumes = "multipart/form-data")
    public ResponseEntity<List<String>> uploadWifiRouters(
            @RequestParam("file") MultipartFile file,
            @RequestParam String token) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(List.of("Please upload a valid Excel file."));
        }

        List<String> responses = service.uploadWifiRouters(file);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/list wifi by location")
    public PaginatedResponseDto listByLocation(
            @RequestParam String location,
            @RequestParam String companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam String token) {
        return service.listByLocation(location, companyId, page);
    }

    @DeleteMapping("/delete wifi router")
    public String delete(@RequestBody WifiRouter wifiRouter, @RequestParam String token) {
    	
        return service.deleteWifiRouter(wifiRouter);
    }

}
