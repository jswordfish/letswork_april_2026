package com.letswork.crm.dtos;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.entities.LetsWorkCentre;

import lombok.Data;

@Data
public class LetsWorkCentreRequestDTO {
	
	private LetsWorkCentre centre;
    private List<MultipartFile> images;

}
