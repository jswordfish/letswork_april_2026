package com.letswork.crm.util;

import java.util.concurrent.TimeUnit;

public class TokenService2 {
	
	private static String sep = "#$#$&&";
	private static String sepPattern = "[\\#][\\$][\\#][\\$][\\&][\\&]";
	
	private static Long sessionTimeOutInDays = 1l;
	
	public static String generateToken(String role, String user) {
		
		Long time = System.currentTimeMillis();
		String input = user+sep+time+sep+role;
		String token = EncDecService.encrypt(input);
		token = role+"$$$"+token;
		return token;
	}
	
	public static String validateTokenAndReturnUserInfo(String token) {
		try {
			//String patt = "[\\#\\$\\#\\$\\&\\&]";
			token = token.substring(token.indexOf("$$$")+3, token.length());
			String decrypted = EncDecService.decrypt(token);
			String dat[] = decrypted.split(sepPattern);
				if(dat.length != 3) {
					return "TOKEN_INVALID";
				}
			String user = dat[0];
			String time = dat[1];
			Long timeInL = Long.parseLong(time);
			Long now = System.currentTimeMillis();
			Long days = TimeUnit.MILLISECONDS.toDays(now - timeInL);
			if(days > sessionTimeOutInDays) {
				return "TOKEN_EXPIRED";
			}
			else{
				String uT = dat[2];
				return user+sep+uT;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("in validateTokenAndReturnUserType "+e.getMessage());
			return "TOKEN_INVALID_("+e.getMessage()+")";
		}
	}

}
