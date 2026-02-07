package com.fixora.maintainance.user.domain.model.request;

import lombok.Data;

@Data
public class AttachmentRequest {
    private String fileName;
    private String fileType; // e.g., "application/pdf"
    private byte[] fileContent;
    private String attachmentType; // e.g., "DEWA", "CONTRACT"
}

