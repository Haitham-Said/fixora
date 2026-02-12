package com.fixora.maintainance.user.infrastructure.storage;

import com.fixora.maintainance.user.domain.service.IStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.InputStream;
import java.util.UUID;

/**
 * S3 Storage Service Implementation
 * Uploads files to AWS S3 bucket
 * Configure via application.properties:
 * - aws.s3.bucket-name
 * - aws.s3.region
 * - aws.access-key-id (optional, can use IAM role)
 * - aws.secret-access-key (optional, can use IAM role)
 */
@Service
public class S3StorageService implements IStorageService {

    private static final Logger logger = LoggerFactory.getLogger(S3StorageService.class);

    private final S3Client s3Client;
    private final String bucketName;

    public S3StorageService(S3Client s3Client, @Value("${aws.s3.bucket-name:fixora-maintenance}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public String uploadFile(String fileName, String fileType, byte[] fileContent) {
        try {
            String uniqueFileName = generateUniqueFileName(fileName);
            String key = "uploads/" + uniqueFileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(fileType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileContent));

            String fileUrl = String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
            logger.info("File uploaded to S3: {}", fileUrl);
            return fileUrl;
        } catch (Exception e) {
            logger.error("Error uploading file to S3: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload file to S3: " + e.getMessage(), e);
        }
    }

    @Override
    public String uploadFile(String fileName, String fileType, InputStream inputStream) {
        try {
            String uniqueFileName = generateUniqueFileName(fileName);
            String key = "uploads/" + uniqueFileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(fileType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, inputStream.available()));

            String fileUrl = String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
            logger.info("File uploaded to S3: {}", fileUrl);
            return fileUrl;
        } catch (Exception e) {
            logger.error("Error uploading file to S3: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload file to S3: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            // Extract key from URL
            String key = extractKeyFromUrl(fileUrl);
            if (key == null) {
                logger.warn("Could not extract key from URL: {}", fileUrl);
                return;
            }

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            logger.info("File deleted from S3: {}", fileUrl);
        } catch (Exception e) {
            logger.error("Error deleting file from S3: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete file from S3: " + e.getMessage(), e);
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    private String extractKeyFromUrl(String fileUrl) {
        if (fileUrl == null || !fileUrl.contains(bucketName)) {
            return null;
        }
        // Extract key from URL like: https://bucket-name.s3.amazonaws.com/uploads/filename
        int keyStartIndex = fileUrl.indexOf(bucketName) + bucketName.length() + 1;
        if (keyStartIndex < fileUrl.length()) {
            return fileUrl.substring(keyStartIndex);
        }
        return null;
    }
}

