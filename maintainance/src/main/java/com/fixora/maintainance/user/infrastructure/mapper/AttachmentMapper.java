package com.fixora.maintainance.user.infrastructure.mapper;

import com.fixora.maintainance.user.domain.model.Attachment;
import com.fixora.maintainance.user.infrastructure.entity.customer.CustomerAttachment;

public class AttachmentMapper {
    
    public static Attachment toDomain(CustomerAttachment entity) {
        if (entity == null) {
            return null;
        }
        return Attachment.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .fileName(entity.getFileName())
                .fileUrl(entity.getFileUrl())
                .fileType(entity.getFileType())
                .attachmentType(entity.getAttachmentType())
                .build();
    }
}

