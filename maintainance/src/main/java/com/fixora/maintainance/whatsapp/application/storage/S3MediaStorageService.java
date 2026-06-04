package com.fixora.maintainance.whatsapp.application.storage;

import com.fixora.maintainance.user.domain.service.IStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * S3 mode for production: downloads media from Twilio and uploads to storage.
 */
@Service
@ConditionalOnProperty(prefix = "app.media", name = "storage-type", havingValue = "s3", matchIfMissing = true)
public class S3MediaStorageService implements MediaStorageService {

    private static final Logger logger = LoggerFactory.getLogger(S3MediaStorageService.class);

    private final IStorageService storageService;
    private final RestTemplate restTemplate;
    private final String accountSid;
    private final String authToken;

    public S3MediaStorageService(IStorageService storageService,
                                 @Value("${twilio.account-sid:}") String accountSid,
                                 @Value("${twilio.auth-token:}") String authToken) {
        this.storageService = storageService;
        this.restTemplate = new RestTemplate();
        this.accountSid = accountSid;
        this.authToken = authToken;
    }

    @PostConstruct
    void logMode() {
        logger.info("WhatsApp media storage mode active: s3");
    }

    @Override
    public String store(String sourceUrl, String contentType) {
        byte[] bytes = downloadFromTwilio(sourceUrl);
        String fileName = "whatsapp-" + System.currentTimeMillis() + extensionFromContentType(contentType);
        return storageService.uploadFile(fileName, contentType != null ? contentType : "application/octet-stream", bytes);
    }

    private byte[] downloadFromTwilio(String url) {
        HttpHeaders headers = new HttpHeaders();
        String auth = Base64.getEncoder().encodeToString((accountSid + ":" + authToken).getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + auth);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        return response.getBody() != null ? response.getBody() : new byte[0];
    }

    private String extensionFromContentType(String contentType) {
        if (contentType == null) return "";
        if (contentType.contains("jpeg") || contentType.contains("jpg")) return ".jpg";
        if (contentType.contains("png")) return ".png";
        if (contentType.contains("gif")) return ".gif";
        if (contentType.contains("video")) return ".mp4";
        return "";
    }
}
