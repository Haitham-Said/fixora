package com.fixora.maintainance.user.application;

import com.fixora.maintainance.user.domain.model.Customer;
import com.fixora.maintainance.user.domain.model.NotificationRequest;
import com.fixora.maintainance.user.domain.model.NotificationType;
import com.fixora.maintainance.user.domain.model.request.AttachmentRequest;
import com.fixora.maintainance.user.domain.model.request.CustomerRegistrationRequest;
import com.fixora.maintainance.user.domain.service.IUserService;
import com.fixora.maintainance.user.domain.service.INotificationService;
import com.fixora.maintainance.user.inbound.model.CustomerRegistrationRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerRegistrationApplicationService {
    
    private final IUserService userService;
    private final INotificationService notificationService;
    
    public CustomerRegistrationApplicationService(IUserService userService,
                                                 INotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }
    
    public Customer registerCustomer(CustomerRegistrationRequestDTO dto) {
        // Convert DTO to domain request
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest();
        registrationRequest.setName(dto.getName());
        registrationRequest.setEmail(dto.getEmail());
        registrationRequest.setPhone(dto.getPhone());
        registrationRequest.setCompanyId(dto.getCompanyId());
        registrationRequest.setBuildingId(dto.getBuildingId());
        registrationRequest.setApartmentId(dto.getApartmentId());
        
        // Map attachments (if any)
        registrationRequest.setAttachments(mapAttachmentRequests(dto));
        
        // Delegate to domain service
        Customer customer = userService.registerCustomer(registrationRequest);
        
        // Send registration notification WITHOUT activation code.
        // The activation code remains INACTIVE after registration and will be
        // activated and sent in a separate email once the admin approves the user.
        sendRegistrationNotification(
            registrationRequest.getEmail(),
            registrationRequest.getName()
        );
        
        return customer;
    }

    /**
     * Maps attachment files from the incoming DTO to domain-level {@link AttachmentRequest}s.
     * Keeps the attachment handling logic out of {@link #registerCustomer} for readability.
     */
    private List<AttachmentRequest> mapAttachmentRequests(CustomerRegistrationRequestDTO dto) {
        if (dto.getAttachments() == null || dto.getAttachments().isEmpty()) {
            return null;
        }

        List<AttachmentRequest> attachmentRequests = new ArrayList<>();

        for (MultipartFile file : dto.getAttachments()) {
            if (file == null || file.isEmpty()) {
                continue;
            }

            try {
                AttachmentRequest attachmentRequest = new AttachmentRequest();
                attachmentRequest.setFileName(file.getOriginalFilename());
                attachmentRequest.setFileType(file.getContentType());
                attachmentRequest.setFileContent(file.getBytes());
                // Determine attachment type based on file name or allow it to be set
                // For now, we'll use a simple heuristic or default
                attachmentRequest.setAttachmentType(determineAttachmentType(file.getOriginalFilename()));
                attachmentRequests.add(attachmentRequest);
            } catch (IOException e) {
                throw new RuntimeException("Error reading attachment file: " + file.getOriginalFilename(), e);
            }
        }

        return attachmentRequests.isEmpty() ? null : attachmentRequests;
    }
    
    private void sendRegistrationNotification(String email, String name) {
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .recipientEmail(email)
                .recipientName(name)
                .notificationType(NotificationType.USER_REGISTRATION_CODE)
                .subject("Registration Received - Pending Approval")
                .message(String.format(
                    "Dear %s,\n\n" +
                    "Thank you for registering. Your registration is pending admin approval.\n\n" +
                    "You will receive another notification with your activation code once your account is approved.\n\n" +
                    "Best regards,\nMaintenance Team",
                    name
                ))
                .build();
        
        notificationService.sendNotification(notificationRequest);
    }
    
    private String determineAttachmentType(String fileName) {
        if (fileName == null) {
            return "OTHER";
        }
        String lowerFileName = fileName.toLowerCase();
        if (lowerFileName.contains("dewa") || lowerFileName.contains("utility")) {
            return "DEWA";
        } else if (lowerFileName.contains("contract") || lowerFileName.contains("lease")) {
            return "CONTRACT";
        } else {
            return "OTHER";
        }
    }
}

