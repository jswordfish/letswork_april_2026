package com.letswork.crm.serviceImpl;

import java.io.File;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Service
public class S3Service {

	private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
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

        // 1️⃣ Upload object (PRIVATE by default – this is good)
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .contentType("image/jpeg")
                        .build(),
                file.toPath()
        );

        return keyName;
    }
    
    public String uploadSolutionImage(
            String bucketName,
            String companyId,
            String centreName,
            String solutionName,
            String fileName,
            File file
    ) {

        String sanitizedCentre =
                centreName.trim().replaceAll("\\s+", "_").toLowerCase();

        String sanitizedSolution =
                solutionName.trim().replaceAll("\\s+", "_").toLowerCase();

        String keyName =
                companyId +
                "/letswork-centres/" +
                sanitizedCentre +
                "/solutions/" +
                sanitizedSolution +
                "/" +
                fileName;

        // 1️⃣ Upload object (PRIVATE by default)
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .contentType("image/jpeg")
                        .build(),
                file.toPath()
        );

        // 2️⃣ Create GET request
        GetObjectRequest getObjectRequest =
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .build();

        return keyName;
    }
    
    public String uploadConferenceRoomImage(
            String bucketName,
            String companyId,
            String centreName,
            String roomName,
            String fileName,
            File file
    ) {

        String sanitizedCentre =
                centreName.trim()
                        .replaceAll("\\s+", "_")
                        .toLowerCase();

        String sanitizedRoom =
                roomName.trim()
                        .replaceAll("\\s+", "_")
                        .toLowerCase();

        String keyName =
                companyId +
                "/letswork-centres/" +
                sanitizedCentre +
                "/conference-rooms/" +
                sanitizedRoom +
                "/" +
                fileName;

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .contentType("image/jpeg")
                        .build(),
                file.toPath()
        );

        return keyName;
    }
    
    public String uploadAmenityImage(
            String bucketName,
            String companyId,
            String amenityName,
            String fileName,
            File file
    ) {

        String sanitizedAmenity =
                amenityName.trim()
                        .replaceAll("\\s+", "_")
                        .toLowerCase();

        String keyName =
                companyId +
                "/amenities/" +
                sanitizedAmenity +
                "/" +
                fileName;

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .contentType("image/jpeg")
                        .build(),
                file.toPath()
        );

        return keyName;
    }
    
    
    public String uploadUserProfileImage(
            String bucketName,
            String companyId,
            String email,
            String fileName,
            File file
    ) {

        String sanitizedEmail =
                email.trim().replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();

        String keyName =
                companyId +
                "/users/" +
                sanitizedEmail +
                "/profile/" +
                fileName;

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .contentType("image/jpeg")
                        .build(),
                file.toPath()
        );

        return keyName;
    }
    
    public String uploadBookDayPassQrCode(
            String bucketName,
            String companyId,
            String email,
            String bookingCode,
            File qrFile
    ) {

        String sanitizedEmail =
                email.trim().replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();

        String keyName =
                companyId +
                "/users/" +
                sanitizedEmail +
                "/day-pass/" +
                bookingCode +
                ".png";

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .contentType("image/png")
                        .build(),
                qrFile.toPath()
        );

//        PresignedGetObjectRequest presignedRequest =
//                s3Presigner.presignGetObject(p -> p
//                        .getObjectRequest(GetObjectRequest.builder()
//                                .bucket(bucketName)
//                                .key(keyName)
//                                .build())
//                        .signatureDuration(Duration.ofDays(7))
//                );

        return keyName;
    }
    
    public String uploadConferenceRoomQrCode(
            String bucketName,
            String companyId,
            String email,
            String bookingCode,
            File qrFile
    ) {

        String sanitizedEmail =
                email.trim()
                     .replaceAll("[^a-zA-Z0-9]", "_")
                     .toLowerCase();

        String keyName =
                companyId +
                "/users/" +
                sanitizedEmail +
                "/conference-room/" +
                bookingCode +
                ".png";

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .contentType("image/png")
                        .build(),
                qrFile.toPath()
        );

//        PresignedGetObjectRequest presignedRequest =
//                s3Presigner.presignGetObject(p -> p
//                        .getObjectRequest(
//                                GetObjectRequest.builder()
//                                        .bucket(bucketName)
//                                        .key(keyName)
//                                        .build()
//                        )
//                        .signatureDuration(Duration.ofDays(7))
//                );

        return keyName;
    }
    
    public String uploadVisitorQrCode(
            String bucketName,
            String companyId,
            String email,
            String bookingCode,
            File qrFile
    ) {

        String sanitizedEmail =
                email.trim().replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();

        String keyName =
                companyId + "/users/" +
                sanitizedEmail + "/Visitors/" +
                bookingCode + ".png";

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .contentType("image/png")
                        .build(),
                qrFile.toPath()
        );

        return keyName;
    }
    
    public static String generateRandomUUIDString() {
        // Generates a 32-character alphanumeric string (plus dashes)
        String uuid = UUID.randomUUID().toString();
        // Remove the dashes
        String randomText = uuid.replace("-", "");
        return randomText;
    }
    
    public String uploadGrevianceImage(
            String bucketName,
            String companyId,
            String email,
            MultipartFile image
    ) {
        try {
            String sanitizedEmail =
                    email.trim().replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();

            String randomText = generateRandomUUIDString();

            String keyName =
                    companyId + "/users/" +
                    sanitizedEmail + "/Greviance/" +
                    randomText + ".png";

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(keyName)
                            .contentType(image.getContentType())
                            .build(),
                    RequestBody.fromBytes(image.getBytes())
            );

            return keyName;

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload greviance image", e);
        }
    }

    
}
