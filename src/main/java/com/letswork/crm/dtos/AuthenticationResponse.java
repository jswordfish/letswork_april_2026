package com.letswork.crm.dtos;

import com.letswork.crm.entities.User;

public class AuthenticationResponse {
	
	String token;
	
	User user;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	

}
