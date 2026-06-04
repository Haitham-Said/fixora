package com.fixora.maintainance.user.application;

import com.fixora.maintainance.user.domain.model.Customer;
import com.fixora.maintainance.user.domain.model.request.AttachmentRequest;
import com.fixora.maintainance.user.domain.model.request.CustomerRegistrationRequest;
import com.fixora.maintainance.user.domain.service.IUserService;
import com.fixora.maintainance.user.inbound.model.CustomerRegistrationRequestDTO;
import com.fixora.security.application.model.UserInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerRegistrationApplicationService {

    private final IUserService userService;

    public CustomerRegistrationApplicationService(IUserService userService) {
        this.userService = userService;
    }

    public Customer registerCustomer(CustomerRegistrationRequestDTO dto, UserInfo actor) {
        if (actor == null) {
            throw new IllegalArgumentException("Authentication required");
        }
        if (!"OPERATION".equalsIgnoreCase(actor.role())) {
            if (actor.companyId() == null || !actor.companyId().equals(dto.getCompanyId())) {
                throw new IllegalArgumentException("Tenant registration is limited to the authenticated company");
            }
        }

        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest();
        registrationRequest.setName(dto.getName());
        registrationRequest.setEmail(dto.getEmail());
        registrationRequest.setPhone(dto.getPhone());
        registrationRequest.setCompanyId(dto.getCompanyId());
        registrationRequest.setBuildingId(dto.getBuildingId());
        registrationRequest.setApartmentId(dto.getApartmentId());

        registrationRequest.setAttachments(mapAttachmentRequests(dto));

        return userService.registerCustomer(registrationRequest);
    }

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
                attachmentRequest.setAttachmentType(determineAttachmentType(file.getOriginalFilename()));
                attachmentRequests.add(attachmentRequest);
            } catch (IOException e) {
                throw new RuntimeException("Error reading attachment file: " + file.getOriginalFilename(), e);
            }
        }

        return attachmentRequests.isEmpty() ? null : attachmentRequests;
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
