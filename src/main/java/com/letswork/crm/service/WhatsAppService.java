package com.letswork.crm.service;



public interface WhatsAppService {
	
	String uploadMedia(String filePath) throws Exception;

    
    void sendImageMessage(String toPhoneNumber, String mediaId, String caption) throws Exception;

    
    void sendBookingQRCode(String toPhoneNumber, String filePath) throws Exception;

}
