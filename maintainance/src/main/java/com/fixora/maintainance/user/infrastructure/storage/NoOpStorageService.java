package com.fixora.maintainance.user.infrastructure.storage;

import com.fixora.maintainance.user.domain.service.IStorageService;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

/**
 * No-operation storage service implementation.
 * This is a placeholder that doesn't actually upload files.
 * 
 * NOTE: This is kept for development/testing. In production, use S3StorageService.
 * To switch to S3, remove @Service from this class and ensure S3StorageService
 * is the only implementation of IStorageService.
 */
// @Service  // Commented out - use S3StorageService instead
public class NoOpStorageService implements IStorageService {

    @Override
    public String uploadFile(String fileName, String fileType, byte[] fileContent) {
        // Placeholder: Return a mock URL instead of actually uploading
        // In production, replace this with actual storage implementation
        String mockUrl = "https://storage.example.com/files/" + UUID.randomUUID() + "/" + fileName;
        // TODO: Implement actual file upload to storage (S3, Azure Blob, etc.)
        return mockUrl;
    }

    @Override
    public String uploadFile(String fileName, String fileType, InputStream inputStream) {
        // Placeholder: Return a mock URL instead of actually uploading
        // In production, replace this with actual storage implementation
        String mockUrl = "https://storage.example.com/files/" + UUID.randomUUID() + "/" + fileName;
        // TODO: Implement actual file upload to storage (S3, Azure Blob, etc.)
        return mockUrl;
    }

    @Override
    public void deleteFile(String fileUrl) {
        // Placeholder: No operation
        // TODO: Implement actual file deletion from storage
    }
}

