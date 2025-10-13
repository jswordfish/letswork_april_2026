package com.LetsWork.CRM.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;





public class EncDecService {
	
	private static DESKeySpec keySpec; 
	private static SecretKeyFactory keyFactory;
	private static SecretKey key;
	
	private static Cipher encCipher;
	private static Cipher decCipher;
	
	private static Boolean init = false;
	
	static {
		try {
			keySpec = new DESKeySpec("BSRF_Secret".getBytes("UTF8")); 
			keyFactory = SecretKeyFactory.getInstance("DES");
			key = keyFactory.generateSecret(keySpec);
			encCipher = Cipher.getInstance("DES"); // cipher is not thread safe
			encCipher.init(Cipher.ENCRYPT_MODE, key);
			
			decCipher =  Cipher.getInstance("DES"); // cipher is not thread safe
			decCipher.init(Cipher.DECRYPT_MODE, key);
			init = true;
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			System.out.println("in enddecservice 1 "+e.getMessage());
			throw new RuntimeException("InvalidKeyException", e);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			System.out.println("in enddecservice 2 "+e.getMessage());
			throw new RuntimeException("UnsupportedEncodingException", e);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			System.out.println("in enddecservice 3 "+e.getMessage());
			throw new RuntimeException("NoSuchAlgorithmException", e);
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			System.out.println("in enddecservice 4 "+e.getMessage());
			throw new RuntimeException("InvalidKeySpecException", e);
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			System.out.println("in enddecservice 4 "+e.getMessage());
			throw new RuntimeException("NoSuchPaddingException", e);
		}
	}
	
	public static synchronized String encrypt(String data) {
		try {
				
			byte[] cleartext = data.getBytes("UTF8");  
			String encryped = Base64.getEncoder().encodeToString(encCipher.doFinal(cleartext));
			return encryped;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("in encrypt "+e.getMessage());
			return "Encryption_Failure";
		} 
	}
	
	public static synchronized String decrypt(String encData) {
		try {
			byte[] encrypedPwdBytes = Base64.getDecoder().decode(encData.getBytes());
			byte[] plainTextPwdBytes = (decCipher.doFinal(encrypedPwdBytes));
			String decryptedOp = new String(plainTextPwdBytes);
			return decryptedOp;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("in decrypt "+e.getMessage());
			return "Deccryption_Failure";
		} 
	}

}

