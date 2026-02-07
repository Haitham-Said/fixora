package com.fixora.maintainance.user.infrastructure.repository;

import com.fixora.maintainance.user.domain.model.Attachment;
import com.fixora.maintainance.user.domain.repositories.IAttachmentRepository;
import com.fixora.maintainance.user.infrastructure.entity.customer.CustomerAttachment;
import com.fixora.maintainance.user.infrastructure.entity.UserEntity;
import com.fixora.maintainance.user.infrastructure.mapper.AttachmentMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AttachmentRepository implements IAttachmentRepository {

    private final AttachmentJpaRepository attachmentJpaRepository;

    public AttachmentRepository(AttachmentJpaRepository attachmentJpaRepository) {
        this.attachmentJpaRepository = attachmentJpaRepository;
    }

    @Override
    @Transactional
    public List<Attachment> saveAttachments(Long userId, List<Attachment> attachments) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        List<CustomerAttachment> entities = attachments.stream()
                .map(attachment -> {
                    CustomerAttachment entity = new CustomerAttachment();
                    entity.setUser(userEntity);
                    entity.setFileName(attachment.getFileName());
                    entity.setFileUrl(attachment.getFileUrl());
                    entity.setFileType(attachment.getFileType());
                    entity.setAttachmentType(attachment.getAttachmentType());
                    return entity;
                })
                .collect(Collectors.toList());

        List<CustomerAttachment> savedEntities = attachmentJpaRepository.saveAll(entities);
        return savedEntities.stream()
                .map(AttachmentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Attachment> findByUserId(Long userId) {
        List<CustomerAttachment> entities = attachmentJpaRepository.findByUserId(userId);
        return entities.stream()
                .map(AttachmentMapper::toDomain)
                .collect(Collectors.toList());
    }
}

