package com.letswork.crm.filters;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.letswork.crm.util.TokenService2;



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
		    	
		    	if(url.endsWith("mcp")) {
		    		chain.doFilter(req, res);
		    		return;
		    	}
		    	
		    	if(url.endsWith("tools")) {
		    		chain.doFilter(req, res);
		    		return;
		    	}
		    	
		    	if(url.endsWith("fetch_knowledge_base")) {
		    		chain.doFilter(req, res);
		    		return;
		    	}
		    	
		    	if(url.endsWith("stream_knowledge_base")) {
		    		chain.doFilter(req, res);
		    		return;
		    	}
		    	
		    	if(url.endsWith("send-otp-register")) {
		    		chain.doFilter(req, res);
		    		return;
		    	}
		    	
		    	if(url.endsWith("send-otp-login")) {
		    		chain.doFilter(req, res);
		    		return;
		    	}
		    	
		    	if(url.endsWith("verify-otp")) {
		    		chain.doFilter(req, res);
		    		return;
		    	}
		    	
		    	if(url.endsWith("registerSend")) {
		    		chain.doFilter(req, res);
		    		return;
		    	}
		    	
		    	if(url.endsWith("loginSend")) {
		    		chain.doFilter(req, res);
		    		return;
		    	}
		    	
		    	if(url.endsWith("verify")) {
		    		chain.doFilter(req, res);
		    		return;
		    	}
		    	
		    	if(url.endsWith("register")) {
		    		chain.doFilter(req, res);
		    		return;
		    	}
		    	
		    	if(url.endsWith("NewUsers-get")) {
		    		chain.doFilter(req, res);
		    		return;
		    	}
		    	
		    	if(url.endsWith("checkRegister")) {
		    		chain.doFilter(req, res);
		    		return;
		    	}
		    	
		    	if(url.endsWith("reset-credits-mail")) {
		    		chain.doFilter(req, res);
		    		return;
		    	}
		    	
		    	if(url.endsWith("reset-monthly-benefits")) {
		    		chain.doFilter(req, res);
		    		return;
		    	}
		    	
		    	if(url.endsWith("qr")) {
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
