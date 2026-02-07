package com.fixora.maintainance.user.domain.service;

import java.io.InputStream;

/**
 * Abstraction for file storage services (S3, Azure Blob, Local, etc.)
 * Implementations can be swapped without changing business logic
 */
public interface IStorageService {
    
    /**
     * Uploads a file to storage and returns the URL
     * @param fileName The name of the file
     * @param fileType The MIME type of the file
     * @param fileContent The file content as byte array
     * @return The URL where the file can be accessed
     */
    String uploadFile(String fileName, String fileType, byte[] fileContent);
    
    /**
     * Uploads a file to storage using InputStream and returns the URL
     * @param fileName The name of the file
     * @param fileType The MIME type of the file
     * @param inputStream The file content as InputStream
     * @return The URL where the file can be accessed
     */
    String uploadFile(String fileName, String fileType, InputStream inputStream);
    
    /**
     * Deletes a file from storage
     * @param fileUrl The URL of the file to delete
     */
    void deleteFile(String fileUrl);
}

