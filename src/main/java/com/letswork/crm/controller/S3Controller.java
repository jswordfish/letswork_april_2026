package com.letswork.crm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.serviceImpl.S3Service;

@RestController
@CrossOrigin
public class S3Controller {
	
	private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping("/check-file")
    public ResponseEntity<String> checkFile(
            @RequestParam String bucketName,
            @RequestParam String keyName,
            @RequestParam String token) {

        boolean exists = s3Service.doesFileExist(bucketName, keyName);

        if (exists) {
            return ResponseEntity.ok("✅ File exists in S3: " + keyName);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("❌ File not found in S3: " + keyName);
        }
    }

}
