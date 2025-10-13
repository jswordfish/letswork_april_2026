package com.LetsWork.CRM;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.LetsWork.CRM.serviceImpl.S3Service;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@SpringBootTest

class CrmApplicationTests {

	//@Test
	void contextLoads() {
	}
	
	@Autowired
    private S3Service s3Service;

    @Autowired
    private S3Client s3Client;

    //@Test
    void testCreateBucketAndUploadImage() {
        String bucketName = "myapp-bucket-" + System.currentTimeMillis(); // unique name
        String keyName = "test-folder/sample-image.png";
        File file = new File("C:\\Users\\User\\Desktop\\Dhruv\\images\\images.png");

        // 1. Create the bucket
        try {
            s3Client.createBucket(CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .createBucketConfiguration(CreateBucketConfiguration.builder()
                            .locationConstraint("ap-south-1") // region must match your client
                            .build())
                    .build());

            System.out.println("✅ Bucket created: " + bucketName);
        } catch (BucketAlreadyExistsException | BucketAlreadyOwnedByYouException e) {
            System.out.println("⚠️ Bucket already exists, skipping creation: " + bucketName);
        }

        // 2. Upload file to the bucket
//        String fileUrl = s3Service.uploadFile(bucketName, keyName, file);
//        System.out.println("✅ File uploaded successfully: " + fileUrl);
    }
    
    //@Test
    void testUploadJarFileToS3() {
        String bucketName = "myapp-bucket-1758037822620"; // ✅ your bucket
        String keyName = "jars/CRM-0.0.1-SNAPSHOT.jar";   // ✅ where in bucket to save
        File jarFile = new File("C:\\Users\\User\\Downloads\\CRM\\CRM\\target\\CRM-0.0.1-SNAPSHOT.jar");

        if (!jarFile.exists()) {
            throw new RuntimeException("❌ Jar file not found at: " + jarFile.getAbsolutePath());
        }

        // Upload JAR file
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .contentType("application/java-archive")
                        .build(),
                jarFile.toPath()
        );

        String fileUrl = "https://" + bucketName + ".s3." +
                s3Client.serviceClientConfiguration().region().id() +
                ".amazonaws.com/" + keyName;

        System.out.println("✅ JAR uploaded successfully: " + fileUrl);
    }

}
