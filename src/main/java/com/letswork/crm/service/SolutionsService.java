package com.letswork.crm.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.letswork.crm.entities.Solutions;

public interface SolutionsService {
	
	public String saveOrUpdate(
            Solutions solution,
            MultipartFile image
    ) throws IOException;

}
