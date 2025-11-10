package com.letswork.crm.serviceImpl;

import java.io.ByteArrayInputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Service
public class S3ServiceMCP {

    private final S3Client s3Client;

    
    private static final String BUCKET_NAME = "myapp-bucket-1758179140483";

    public S3ServiceMCP(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Fetch the file content as string from S3
     */
    public String getFileContent(String key) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .build();

            ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(request);
            return response.asUtf8String();

        } catch (Exception e) {
            throw new RuntimeException("Error fetching file from S3: " + e.getMessage(), e);
        }
    }
    
    public String getPdfAsText(String key) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .build();

            ResponseBytes<GetObjectResponse> bytes = s3Client.getObjectAsBytes(request);

            try (PDDocument document = PDDocument.load(new ByteArrayInputStream(bytes.asByteArray()))) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching or reading PDF: " + e.getMessage();
        }
    }
    
}
