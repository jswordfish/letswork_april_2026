package com.letswork.crm.dtos;

import com.letswork.crm.entities.Lead;
import com.letswork.crm.entities.User;

import lombok.Data;

@Data
public class CommentResponseDto {
	
	private Long id;
	
	private Long leadId;

    private Long userId;

    private String comment;

    private Lead lead;

    private User user;

}
