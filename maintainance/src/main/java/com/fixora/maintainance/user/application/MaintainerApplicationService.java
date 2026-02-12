package com.fixora.maintainance.user.application;

import com.fixora.maintainance.user.domain.model.Maintainer;
import com.fixora.maintainance.user.domain.model.NotificationRequest;
import com.fixora.maintainance.user.domain.model.NotificationType;
import com.fixora.maintainance.user.domain.model.Role;
import com.fixora.maintainance.user.domain.model.UserCode;
import com.fixora.maintainance.user.domain.model.request.MaintainerRequest;
import com.fixora.maintainance.user.domain.repositories.IUserCodeRepository;
import com.fixora.maintainance.user.domain.service.INotificationService;
import com.fixora.maintainance.user.inbound.model.MaintainerRequestDTO;
import com.fixora.maintainance.user.domain.service.IUserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MaintainerApplicationService {
    private final IUserService userService;
    private final IUserCodeRepository userCodeRepository;
    private final INotificationService notificationService;

    public MaintainerApplicationService(IUserService userService,
                                       IUserCodeRepository userCodeRepository,
                                       INotificationService notificationService) {
        this.userService = userService;
        this.userCodeRepository = userCodeRepository;
        this.notificationService = notificationService;
    }

    public Maintainer addMaintainer(MaintainerRequestDTO maintainerRequestDTO){
        // Convert DTO to domain request
        MaintainerRequest maintainerRequest = new MaintainerRequest();
        maintainerRequest.setCompanyId(maintainerRequestDTO.getUserDetails().getCompanyId());
        maintainerRequest.setName(maintainerRequestDTO.getUserDetails().getName());
        maintainerRequest.setEmail(maintainerRequestDTO.getUserDetails().getEmail());
        maintainerRequest.setPhone(maintainerRequestDTO.getUserDetails().getPhone());
        maintainerRequest.setRole(Role.MAINTAINER.name());

        // Delegate to domain service to create user and maintainer
        Maintainer maintainer = userService.addMaintainer(maintainerRequest);
        
        // Generate activation code and send invitation email
        Optional<UserCode> userCodeOpt = userCodeRepository.findByUserId(maintainer.getUser().getId());
        if (userCodeOpt.isPresent()) {
            UserCode userCode = userCodeOpt.get();
            sendMaintainerInvitationNotification(
                maintainerRequest.getEmail(),
                maintainerRequest.getName(),
                userCode.getCode()
            );
        }
        
        return maintainer;
    }
    
    private void sendMaintainerInvitationNotification(String email, String name, String code) {
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .recipientEmail(email)
                .recipientName(name)
                .notificationType(NotificationType.USER_ACTIVATION_CODE)
                .subject("Welcome! Your Maintenance Account Activation Code")
                .message(String.format(
                    "Dear %s,\n\n" +
                    "Your maintenance account has been created successfully. " +
                    "Your activation code is: %s\n\n" +
                    "Please download the maintenance app and use this code to complete your profile and set your password.\n\n" +
                    "This code will expire after first use.\n\n" +
                    "Best regards,\nMaintenance Team",
                    name, code
                ))
                .activationCode(code)
                .build();
        
        notificationService.sendNotification(notificationRequest);
    }
}
