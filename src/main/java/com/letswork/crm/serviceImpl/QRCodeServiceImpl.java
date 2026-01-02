package com.letswork.crm.serviceImpl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.letswork.crm.service.QRCodeService;



@Service
@Transactional
public class QRCodeServiceImpl implements QRCodeService {
	
	private static final String QR_CODE_DIR = "C:\\Users\\hp\\Desktop\\Dhruv2025\\Images\\";

	@Override
	public String generateQRCode(String text, String fileName) throws WriterException, IOException {
		// TODO Auto-generated method stub
		
        File directory = new File(QR_CODE_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        
        int width = 300;
        int height = 300;

        
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        
        String filePath = QR_CODE_DIR + fileName + ".png";
        Path path = FileSystems.getDefault().getPath(filePath);

        
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

        return filePath;
	}

	@Override
	public String generateQRCodeWithBookingCode(String bookingCode) throws Exception {
		// TODO Auto-generated method stub
		Path path = Paths.get(QR_CODE_DIR + bookingCode + ".png");
        Files.createDirectories(path.getParent());

        BitMatrix matrix = new MultiFormatWriter()
                .encode(bookingCode, BarcodeFormat.QR_CODE, 250, 250);

        MatrixToImageWriter.writeToPath(matrix, "PNG", path);

        return path.toString();
    }
	
	@Override
	public String generateQRCodeWithBookingCodeRGB(String bookingCode) throws Exception {

	    String baseDir = QR_CODE_DIR; 

	    if (baseDir == null || baseDir.isBlank()) {
	        throw new IllegalStateException("QR_CODE_DIR is not configured");
	    }

	    Path dirPath = Paths.get(baseDir);
	    Files.createDirectories(dirPath); 

	    // 2️⃣ File path
	    Path filePath = dirPath.resolve(bookingCode + ".png");

	    BitMatrix matrix = new MultiFormatWriter()
	            .encode(bookingCode, BarcodeFormat.QR_CODE, 250, 250);

	    BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(matrix);

	    BufferedImage rgbImage = new BufferedImage(
	            qrImage.getWidth(),
	            qrImage.getHeight(),
	            BufferedImage.TYPE_INT_RGB
	    );

	    Graphics2D g = rgbImage.createGraphics();
	    g.drawImage(qrImage, 0, 0, Color.WHITE, null);
	    g.dispose();

	    ImageIO.write(rgbImage, "png", filePath.toFile());

	    return filePath.toAbsolutePath().toString();
	}
	
}
	
	


