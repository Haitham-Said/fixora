package com.fixora.maintainance.whatsapp.application.storage;

/**
 * Strategy abstraction for WhatsApp media persistence.
 * Implementations can either upload to S3 or keep source URL directly.
 */
public interface MediaStorageService {

    /**
     * Persists media and returns the final URL to store in DB.
     */
    String store(String sourceUrl, String contentType);
}
