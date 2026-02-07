package com.fixora.maintainance.user.domain.repositories;

import com.fixora.maintainance.user.domain.model.Attachment;

import java.util.List;

public interface IAttachmentRepository {
    
    /**
     * Saves attachments for a user
     * @param userId The user ID
     * @param attachments List of attachments to save
     * @return List of saved attachments
     */
    List<Attachment> saveAttachments(Long userId, List<Attachment> attachments);
    
    /**
     * Finds all attachments for a user
     * @param userId The user ID
     * @return List of attachments
     */
    List<Attachment> findByUserId(Long userId);
}

