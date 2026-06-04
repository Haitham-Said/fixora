package com.fixora.maintainance.whatsapp.application;

import com.fixora.maintainance.whatsapp.application.storage.MediaStorageService;
import org.springframework.stereotype.Service;

/**
 * Downloads media from Twilio URLs (with auth) and stores to our storage.
 */
@Service
public class MediaService {

    private final MediaStorageService mediaStorageService;

    public MediaService(MediaStorageService mediaStorageService) {
        this.mediaStorageService = mediaStorageService;
    }

    /**
     * Download from Twilio media URL and upload to our storage.
     * Returns storage URL.
     */
    public String downloadAndStore(String twilioMediaUrl, String contentType) {
        return mediaStorageService.store(twilioMediaUrl, contentType);
    }
}

