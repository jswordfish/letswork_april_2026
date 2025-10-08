package com.LetsWork.CRM.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LetsWork.CRM.dtos.PaginatedResponseDto;
import com.LetsWork.CRM.entities.WifiRouter;
import com.LetsWork.CRM.service.WifiRouterService;


@RestController
@CrossOrigin
public class WifiRouterController {
	
	@Autowired
    private WifiRouterService service;

    @PostMapping("/create or update wifi router")
    public String saveOrUpdate(@RequestBody WifiRouter wifiRouter, @RequestParam String token) {
    	
        return service.saveOrUpdate(wifiRouter);
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
