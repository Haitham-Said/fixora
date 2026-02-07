package com.fixora.maintainance.user.inbound.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CustomerRegistrationRequestDTO {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "Phone is required")
    private String phone;
    
    @NotNull(message = "Company ID is required")
    private Long companyId;
    
    @NotNull(message = "Building ID is required")
    private Long buildingId;
    
    @NotNull(message = "Apartment ID is required")
    private Long apartmentId;
    
    @Size(max = 2, message = "Maximum 2 attachments allowed")
    private List<MultipartFile> attachments;
}

