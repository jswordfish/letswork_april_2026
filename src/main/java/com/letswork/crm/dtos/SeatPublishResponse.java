package com.letswork.crm.dtos;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SeatPublishResponse {
	
	private List<String> published = new ArrayList<>();
    private List<String> alreadyPublished = new ArrayList<>();
    private List<String> notFound = new ArrayList<>();

}
