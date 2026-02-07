package com.fixora.maintainance.user.application;

import com.fixora.maintainance.user.domain.model.Attachment;
import com.fixora.maintainance.user.domain.model.InactiveUser;
import com.fixora.maintainance.user.domain.model.NotificationRequest;
import com.fixora.maintainance.user.domain.model.NotificationType;
import com.fixora.maintainance.user.domain.model.UserCode;
import com.fixora.maintainance.user.domain.model.request.UserActivationRequest;
import com.fixora.maintainance.user.domain.repositories.IUserCodeRepository;
import com.fixora.maintainance.user.domain.service.IUserService;
import com.fixora.maintainance.user.domain.service.INotificationService;
import com.fixora.maintainance.user.inbound.model.AttachmentResponseDTO;
import com.fixora.maintainance.user.inbound.model.InactiveUserResponseDTO;
import com.fixora.maintainance.user.inbound.model.UserActivationRequestDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminUserApplicationService {

    private final IUserService userService;
    private final IUserCodeRepository userCodeRepository;
    private final INotificationService notificationService;

    public AdminUserApplicationService(IUserService userService,
                                     IUserCodeRepository userCodeRepository,
                                     INotificationService notificationService) {
        this.userService = userService;
        this.userCodeRepository = userCodeRepository;
        this.notificationService = notificationService;
    }

    public List<InactiveUserResponseDTO> getInactiveUsers() {
        List<InactiveUser> inactiveUsers = userService.getInactiveUsers();
        return inactiveUsers.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public InactiveUserResponseDTO getInactiveUserById(Long userId) {
        InactiveUser inactiveUser = userService.getInactiveUserById(userId);
        return toDTO(inactiveUser);
    }

    public void activateUser(UserActivationRequestDTO activationRequestDTO) {
        UserActivationRequest activationRequest = new UserActivationRequest();
        activationRequest.setUserId(activationRequestDTO.getUserId());
        // No password needed - user will set password using activation code
        
        // Activate user (generates code in domain service)
        userService.activateUser(activationRequest);
        
        // Get user details and code for notification
        InactiveUser inactiveUser = userService.getInactiveUserById(activationRequestDTO.getUserId());
        if (inactiveUser != null) {
            Optional<UserCode> userCodeOpt = userCodeRepository.findByUserId(activationRequestDTO.getUserId());
            if (userCodeOpt.isPresent()) {
                UserCode userCode = userCodeOpt.get();
                sendActivationNotification(inactiveUser.getEmail(), inactiveUser.getName(), userCode.getCode());
            }
        }
    }
    
    private void sendActivationNotification(String email, String name, String code) {
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .recipientEmail(email)
                .recipientName(name)
                .notificationType(NotificationType.USER_ACTIVATION_CODE)
                .subject("Account Activated - Activation Code")
                .message(String.format(
                    "Dear %s,\n\n" +
                    "Your account has been approved and activated. " +
                    "Your activation code is: %s\n\n" +
                    "Please use this code to complete your account setup.\n\n" +
                    "Best regards,\nMaintenance Team",
                    name, code
                ))
                .activationCode(code)
                .build();
        
        notificationService.sendNotification(notificationRequest);
    }

    private InactiveUserResponseDTO toDTO(InactiveUser inactiveUser) {
        InactiveUserResponseDTO dto = new InactiveUserResponseDTO();
        dto.setUserId(inactiveUser.getUserId());
        dto.setName(inactiveUser.getName());
        dto.setEmail(inactiveUser.getEmail());
        dto.setPhone(inactiveUser.getPhone());
        dto.setCompanyId(inactiveUser.getCompanyId());
        dto.setCompanyName(inactiveUser.getCompanyName());
        dto.setBuildingId(inactiveUser.getBuildingId());
        dto.setBuildingName(inactiveUser.getBuildingName());
        dto.setApartmentId(inactiveUser.getApartmentId());
        dto.setApartmentNumber(inactiveUser.getApartmentNumber());
        dto.setRegisteredAt(inactiveUser.getRegisteredAt());
        
        if (inactiveUser.getAttachments() != null) {
            List<AttachmentResponseDTO> attachmentDTOs = inactiveUser.getAttachments().stream()
                    .map(this::toAttachmentDTO)
                    .collect(Collectors.toList());
            dto.setAttachments(attachmentDTOs);
        }
        
        return dto;
    }

    private AttachmentResponseDTO toAttachmentDTO(Attachment attachment) {
        AttachmentResponseDTO dto = new AttachmentResponseDTO();
        dto.setId(attachment.getId());
        dto.setFileName(attachment.getFileName());
        dto.setFileUrl(attachment.getFileUrl());
        dto.setFileType(attachment.getFileType());
        dto.setAttachmentType(attachment.getAttachmentType());
        return dto;
    }
}

