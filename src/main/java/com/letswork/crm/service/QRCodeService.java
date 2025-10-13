package com.LetsWork.CRM.service;

import java.io.IOException;

import com.google.zxing.WriterException;

public interface QRCodeService {
	
	public String generateQRCode(String text, String fileName) throws WriterException, IOException;
	
	public String generateQRCodeWithBookingCode(String bookingCode) throws Exception;
	
	public String generateQRCodeWithBookingCodeRGB(String bookingCode) throws Exception;

}
