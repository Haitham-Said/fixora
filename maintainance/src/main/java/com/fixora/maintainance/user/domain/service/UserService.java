package com.fixora.maintainance.user.domain.service;

import com.fixora.maintainance.user.domain.model.Customer;
import com.fixora.maintainance.user.domain.model.InactiveUser;
import com.fixora.maintainance.user.domain.model.Maintainer;
import com.fixora.maintainance.user.domain.model.Role;
import com.fixora.maintainance.user.domain.model.request.CustomerRequest;
import com.fixora.maintainance.user.domain.model.request.CustomerRegistrationRequest;
import com.fixora.maintainance.user.domain.model.request.MaintainerRequest;
import com.fixora.maintainance.user.domain.model.request.UserActivationRequest;
import com.fixora.maintainance.user.infrastructure.entity.UserEntity;
import com.fixora.maintainance.user.domain.exception.UserNotFoundException;
import com.fixora.maintainance.user.domain.repositories.IUserRepository;
import com.fixora.maintainance.user.domain.repositories.IMaintainerRepository;
import com.fixora.maintainance.user.domain.repositories.ICustomerRepository;
import com.fixora.maintainance.user.domain.repositories.IAttachmentRepository;
import com.fixora.maintainance.user.domain.repositories.IUserCodeRepository;
import com.fixora.maintainance.user.domain.service.IStorageService;
import com.fixora.maintainance.user.domain.service.CodeGeneratorService;
import com.fixora.maintainance.user.domain.model.Attachment;
import com.fixora.maintainance.user.domain.model.UserCode;
import com.fixora.maintainance.property.domain.repository.IApartmentRepository;
import com.fixora.maintainance.property.domain.service.BuildingService;
import org.springframework.stereotype.Component;
import com.fixora.maintainance.user.domain.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final IMaintainerRepository maintainerRepository;
    private final ICustomerRepository customerRepository;
    private final IAttachmentRepository attachmentRepository;
    private final IUserCodeRepository userCodeRepository;
    private final IStorageService storageService;
    private final CodeGeneratorService codeGeneratorService;
    private final BuildingService buildingService;
    private final IApartmentRepository apartmentRepository;

    public UserService(IUserRepository userRepository, IMaintainerRepository maintainerRepository, 
                       ICustomerRepository customerRepository, IAttachmentRepository attachmentRepository,
                       IUserCodeRepository userCodeRepository, IStorageService storageService,
                       CodeGeneratorService codeGeneratorService, BuildingService buildingService,
                       IApartmentRepository apartmentRepository) {
        this.userRepository = userRepository;
        this.maintainerRepository = maintainerRepository;
        this.customerRepository = customerRepository;
        this.attachmentRepository = attachmentRepository;
        this.userCodeRepository = userCodeRepository;
        this.storageService = storageService;
        this.codeGeneratorService = codeGeneratorService;
        this.buildingService = buildingService;
        this.apartmentRepository = apartmentRepository;
    }

    public UserEntity findUserByEmail(String username) {
         return userRepository.findUserByUsername(username)
                 .orElseThrow(()->new UserNotFoundException("user not exist"));

    }

    public List<Maintainer> findAvailableMaintainersForSlotAndCompany(String preferredSlot,Long id){
        return userRepository.findAvailableMaintainersForSlotAndCompany(preferredSlot,id);
    }

    public Maintainer addMaintainer(MaintainerRequest maintainerRequest){
        User user = userRepository.addUser(maintainerRequest);
        Maintainer maintainer = maintainerRepository.addMaintainer(user);
        return maintainer;
    }

    public Customer addCustomer(CustomerRequest customerRequest){
        // Business logic: Find building by code
        com.fixora.maintainance.property.domain.model.Building building = buildingService.getBuildingByBuildingCode(customerRequest.getBuildingCode());
        if (building == null) {
            throw new IllegalArgumentException("Building not found with code: " + customerRequest.getBuildingCode());
        }

        // Business logic: Find apartment by building and apartment number
        com.fixora.maintainance.property.domain.model.Apartment apartment = apartmentRepository.findByBuildingIdAndApartmentNumber(
                building.getId(), customerRequest.getApartmentNumber());
        if (apartment == null) {
            throw new IllegalArgumentException("Apartment not found with building code: " + customerRequest.getBuildingCode() 
                    + " and apartment number: " + customerRequest.getApartmentNumber());
        }

        // Create user with CUSTOMER role
        User user = userRepository.addUser(
                customerRequest.getName(),
                customerRequest.getEmail(),
                customerRequest.getPhone() != null ? customerRequest.getPhone() : "",
                Role.CUSTOMER.name(),
                building.getCompanyId()
        );

        // Create customer record
        Customer customer = customerRepository.addCustomer(user, apartment.getId(), customerRequest.getMoveInDate());
        
        // Generate activation code with ACTIVE status (tenant upload means they're already approved)
        generateAndSaveUserCode(user.getId(), "ACTIVE");
        
        return customer;
    }

    public Customer registerCustomer(CustomerRegistrationRequest registrationRequest) {
        // Validate apartment exists and belongs to the specified building and company
        com.fixora.maintainance.property.domain.model.Apartment apartment = 
                apartmentRepository.findById(registrationRequest.getApartmentId());
        if (apartment == null) {
            throw new IllegalArgumentException("Apartment not found with ID: " + registrationRequest.getApartmentId());
        }

        // Validate building matches
        if (apartment.getBuildingId() == null || !apartment.getBuildingId().equals(registrationRequest.getBuildingId())) {
            throw new IllegalArgumentException("Apartment does not belong to the specified building");
        }

        // Validate company matches (through building)
        com.fixora.maintainance.property.domain.model.Building building = 
                buildingService.getBuildingById(registrationRequest.getBuildingId());
        if (building == null) {
            throw new IllegalArgumentException("Building not found with ID: " + registrationRequest.getBuildingId());
        }
        if (!building.getCompanyId().equals(registrationRequest.getCompanyId())) {
            throw new IllegalArgumentException("Building does not belong to the specified company");
        }

        // Create user with CUSTOMER role and INACTIVE status
        User user = userRepository.addUserWithStatus(
                registrationRequest.getName(),
                registrationRequest.getEmail(),
                registrationRequest.getPhone(),
                Role.CUSTOMER.name(),
                registrationRequest.getCompanyId(),
                "INACTIVE" // Status set to INACTIVE for self-registration
        );

        // Create customer record
        Customer customer = customerRepository.addCustomer(user, apartment.getId(), null);

        // Process and save attachments if provided
        if (registrationRequest.getAttachments() != null && !registrationRequest.getAttachments().isEmpty()) {
            List<Attachment> attachments = registrationRequest.getAttachments().stream()
                    .map(attachmentRequest -> {
                        // Upload file to storage (returns URL)
                        String fileUrl = storageService.uploadFile(
                                attachmentRequest.getFileName(),
                                attachmentRequest.getFileType(),
                                attachmentRequest.getFileContent()
                        );

                        // Create attachment domain model
                        return Attachment.builder()
                                .userId(user.getId())
                                .fileName(attachmentRequest.getFileName())
                                .fileUrl(fileUrl)
                                .fileType(attachmentRequest.getFileType())
                                .attachmentType(attachmentRequest.getAttachmentType())
                                .build();
                    })
                    .collect(Collectors.toList());

            // Save attachments to database
            attachmentRepository.saveAttachments(user.getId(), attachments);
        }

        // Generate activation code with INACTIVE status (pending admin approval)
        generateAndSaveUserCode(user.getId(), "INACTIVE");

        return customer;
    }

    public List<InactiveUser> getInactiveUsers() {
        return userRepository.findInactiveUsers();
    }

    public InactiveUser getInactiveUserById(Long userId) {
        return userRepository.findInactiveUserById(userId);
    }

    public void activateUser(UserActivationRequest activationRequest) {
        // Update user status to ACTIVE (no password needed)
        userRepository.activateUser(activationRequest.getUserId());
        
        // Generate or update activation code with ACTIVE status
        generateAndSaveUserCode(activationRequest.getUserId(), "ACTIVE");
    }
    
    /**
     * Generates and saves a user code
     * @param userId The user ID
     * @param status The status (ACTIVE or INACTIVE)
     * @return The generated code
     */
    public String generateAndSaveUserCode(Long userId, String status) {
        String code = codeGeneratorService.generateCode();
        
        // Ensure code is unique
        while (userCodeRepository.findByCode(code).isPresent()) {
            code = codeGeneratorService.generateCode();
        }
        
        UserCode userCode = UserCode.builder()
                .userId(userId)
                .code(code)
                .status(status)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(30)) // Code expires in 30 days
                .build();
        
        userCodeRepository.save(userCode);
        return code;
    }
}
