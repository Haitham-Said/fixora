package com.fixora.maintainance.user.inbound.model;

import lombok.Data;

@Data
public class AttachmentResponseDTO {
    private Long id;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private String attachmentType;
}

