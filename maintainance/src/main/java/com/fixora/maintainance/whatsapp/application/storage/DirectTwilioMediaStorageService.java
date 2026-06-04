package com.fixora.maintainance.whatsapp.application.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * Direct mode for local/testing: keeps Twilio media URL without S3 upload.
 */
@Service
@ConditionalOnProperty(prefix = "app.media", name = "storage-type", havingValue = "direct")
public class DirectTwilioMediaStorageService implements MediaStorageService {

    private static final Logger logger = LoggerFactory.getLogger(DirectTwilioMediaStorageService.class);

    @PostConstruct
    void logMode() {
        logger.info("WhatsApp media storage mode active: direct (Twilio URL passthrough)");
    }

    @Override
    public String store(String sourceUrl, String contentType) {
        return sourceUrl;
    }
}
