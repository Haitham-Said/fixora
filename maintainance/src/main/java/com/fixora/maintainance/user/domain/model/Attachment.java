package com.fixora.maintainance.user.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Attachment {
    private Long id;
    private Long userId;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private String attachmentType;
}

