package com.letswork.crm.dtos;

import com.letswork.crm.entities.Lead;
import com.letswork.crm.entities.User;

public class LeadResponseDto extends Lead{
	
	private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
