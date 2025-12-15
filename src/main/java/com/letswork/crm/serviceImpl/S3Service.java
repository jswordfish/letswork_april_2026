package com.letswork.crm.serviceImpl;

import java.io.File;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class S3Service {

	private final S3Client s3Client;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }
    
   
    public String uploadQRCode(String bucketName, String companyId, String email, String fileName, File file) {
        
        String keyName = companyId + "/bookingConferenceRoom/" + email + "/" + fileName;

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .contentType("image/png")
                        .build(),
                file.toPath()
        );

        String region = s3Client.serviceClientConfiguration().region().id();
        String endpoint = region.equals("us-east-1")
                ? "https://" + bucketName + ".s3.amazonaws.com/"
                : "https://" + bucketName + ".s3." + region + ".amazonaws.com/";

        return endpoint + keyName;
    }
    
    public boolean doesFileExist(String bucketName, String keyName) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build());
            return true; // File exists
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false; // File not found
            }
            throw e; // Re-throw other S3 errors
        }
    }
    
    public String uploadLandlordDocument(String bucketName, String companyId, String landlordName,
            String subFolder, String fileName, File file) {

		String sanitizedLandLordName = landlordName == null ? "unknown" : landlordName.trim().replaceAll("\\s+", "_");
		String keyName = companyId + "/landlord/" + sanitizedLandLordName + "/" + subFolder + "/" + fileName;
		
		s3Client.putObject(
		PutObjectRequest.builder()
		.bucket(bucketName)
		.key(keyName)
		.build(),
		RequestBody.fromFile(file)
		);
		
		String region = s3Client.serviceClientConfiguration().region().id();
		String endpoint = region.equals("us-east-1")
		? "https://" + bucketName + ".s3.amazonaws.com/"
		: "https://" + bucketName + ".s3." + region + ".amazonaws.com/";
		
		return endpoint + keyName;
}
    
    public void deleteLandlordDocument(String bucketName, String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        String region = s3Client.serviceClientConfiguration().region().id();
        String prefix = region.equals("us-east-1")
            ? "https://" + bucketName + ".s3.amazonaws.com/"
            : "https://" + bucketName + ".s3." + region + ".amazonaws.com/";

        if (!fileUrl.startsWith(prefix)) {
            throw new IllegalArgumentException("URL does not match expected prefix: " + fileUrl);
        }

        String key = fileUrl.substring(prefix.length());

        s3Client.deleteObject(DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build());
    }
    
    public String uploadLetsWorkCentreImage(
            String bucketName,
            String companyId,
            String centreName,
            String fileName,
            File file
    ) {
        String sanitizedCentre =
                centreName.trim().replaceAll("\\s+", "_").toLowerCase();

        String keyName =
                companyId + "/letswork-centres/" + sanitizedCentre + "/" + fileName;

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .contentType("image/jpeg")
                        .build(),
                file.toPath()
        );

        String region = s3Client.serviceClientConfiguration().region().id();
        String endpoint = region.equals("us-east-1")
                ? "https://" + bucketName + ".s3.amazonaws.com/"
                : "https://" + bucketName + ".s3." + region + ".amazonaws.com/";

        return endpoint + keyName;
    }

    
}
