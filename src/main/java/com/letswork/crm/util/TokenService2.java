package com.letswork.crm.util;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.letswork.crm.entities.NewUserRegister;
import com.letswork.crm.entities.User;
import com.letswork.crm.repo.NewUserRegisterRepository;
import com.letswork.crm.repo.UserRepo;

@Component
public class TokenService2 {
	
	@Autowired
	NewUserRegisterRepository newUserRepo;
	
	@Autowired
	UserRepo userRepo;
	
	private static String sep = "#$#$&&";
	private static String sepPattern = "[\\#][\\$][\\#][\\$][\\&][\\&]";
	
	private static Long sessionTimeOutInDays = 30l;
	
	public static String generateToken(String role, String user) {
		
		Long time = System.currentTimeMillis();
		String input = user+sep+time+sep+role;
		String token = EncDecService.encrypt(input);
		token = role+"$$$"+token;
		return token;
	}
	
	public String validateTokenAndReturnUserInfo(String token) {
	    try {
	        token = token.substring(token.indexOf("$$$") + 3, token.length());
	        String decrypted = EncDecService.decrypt(token);
	        String dat[] = decrypted.split(sepPattern);

	        if (dat.length != 3) {
	            return "TOKEN_INVALID";
	        }

	        String user = dat[0];

	        // Skip validation for hardcoded super admin
	        if (!"admin@letswork.com".equals(user)) {

	            Optional<NewUserRegister> newUserOpt =
	                    newUserRepo.findByEmail(user);

	            if (newUserOpt.isPresent()) {

	                if (Boolean.FALSE.equals(newUserOpt.get().getActive())) {
	                    return "USER_DEACTIVATED";
	                }

	            } else {

	                User internalUser =
	                        userRepo.findByEmail(user); 

	                if (internalUser == null) {
	                    return "TOKEN_INVALID";
	                }

	                
	            }
	        }

	        String time = dat[1];
	        Long timeInL = Long.parseLong(time);
	        Long now = System.currentTimeMillis();
	        Long days = TimeUnit.MILLISECONDS.toDays(now - timeInL);

	        if (days > sessionTimeOutInDays) {
	            return "TOKEN_EXPIRED";
	        } else {
	            String uT = dat[2];
	            return user + sep + uT;
	        }

	    } catch (Exception e) {
	        System.out.println("in validateTokenAndReturnUserType " + e.getMessage());
	        return "TOKEN_INVALID_(" + e.getMessage() + ")";
	    }
	}

}
