package com.LetsWork.CRM.filters;

import java.io.IOException;
import java.util.Enumeration;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.LetsWork.CRM.util.TokenService2;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthenticationFilter implements Filter {
	
	private static String sep = "#$#$&&";
	private static String sepPattern = "[\\#][\\$][\\#][\\$][\\&][\\&]";

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		 HttpServletResponse response = (HttpServletResponse) res;
		 
		 response.setHeader("Access-Control-Allow-Origin", "*");
	        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH");
	        response.setHeader("Access-Control-Max-Age", "3600");
	        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
	        response.setHeader("Access-Control-Expose-Headers", "Location");
		 
		    //HttpServletRequest request = (HttpServletRequest) req;

		    HttpServletRequest httpRequest = (HttpServletRequest) req;
		    String url = httpRequest.getRequestURL().toString();
		    if(url.contains("interviews")) {
		    	chain.doFilter(req, res);
	    		return;
		    }
		    
		    	if(url.endsWith("token") || url.contains("jsp")) {
		    		chain.doFilter(req, res);
		    		return;
		    	}
		    	
		    	if(url.endsWith("token2") || url.contains("jsp")) {
		    		chain.doFilter(req, res);
		    		return;
		    	}
		    	
		    	if(url.contains("swagger") || url.contains("api-docs")) {
		    		chain.doFilter(req, res);
		    		return;
		    	}
		    	
		    	if(url.endsWith("fetchTenants") || url.contains("html")) {
		    		chain.doFilter(req, res);
		    		return;
		    	}
		    	
		    	if(url.endsWith("initRolePlayTest")  || url.contains("initRolePlayTest")) {
		    		chain.doFilter(req, res);
		    		return;
		    	}
		    	
		    	
		    String token = httpRequest.getParameter("token");
		    Enumeration<String> headerNames = httpRequest.getHeaderNames();
		    boolean validToken = false;
		    String val = TokenService2.validateTokenAndReturnUserInfo(token);
    		//System.out.println("2222222222222222 val "+val);
    			if(val.startsWith("TOKEN_INVALID_")) {
    				 ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token is not valid.");
    			}
    		String v[] = val.split(sepPattern);
    		//System.out.println("2222222222222222 ********* "+v);
    			if(v != null  && v.length == 2) {
    				String user = v[0];
    	    		String role = v[1];
    	    		httpRequest.setAttribute("role", role);
    	    		httpRequest.setAttribute("user", user);
    	    		validToken = true;
    			}
    		
		    if(validToken) {
		    	chain.doFilter(req, res);
		    }
		    else {
		    	 System.out.println("3333333333333333333333333333333 "+url);
		    	 ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "No token present.");
		    }
		    
	}

}
