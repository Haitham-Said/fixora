package com.fixora.maintainance.user.infrastructure.repository;

import com.fixora.maintainance.user.domain.model.Maintainer;
import com.fixora.maintainance.user.domain.model.request.MaintainerRequest;
import com.fixora.maintainance.user.infrastructure.entity.UserEntity;
import com.fixora.maintainance.user.domain.repositories.IUserRepository;
import com.fixora.maintainance.user.infrastructure.mapper.MaintainerMapper;
import com.fixora.maintainance.user.infrastructure.mapper.UserMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import com.fixora.maintainance.user.domain.model.InactiveUser;
import com.fixora.maintainance.user.domain.model.Attachment;
import com.fixora.maintainance.user.infrastructure.entity.customer.Customer;
import com.fixora.maintainance.user.infrastructure.entity.customer.CustomerAttachment;
import com.fixora.maintainance.user.infrastructure.repository.AttachmentJpaRepository;
import com.fixora.maintainance.user.infrastructure.repository.CustomerJpaRepository;
import com.fixora.maintainance.property.infrastructure.persistence.repository.CompanyJPARepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.fixora.maintainance.property.infrastructure.entity.Company;

@Component
public class UserRepository implements IUserRepository {

    private final UserJpaRepository userJpaRepository;
    private final CustomerJpaRepository customerJpaRepository;
    private final AttachmentJpaRepository attachmentJpaRepository;
    private final CompanyJPARepository companyJPARepository;

    public UserRepository(UserJpaRepository userJpaRepository,
                         CustomerJpaRepository customerJpaRepository,
                         AttachmentJpaRepository attachmentJpaRepository,
                         CompanyJPARepository companyJPARepository) {
        this.userJpaRepository = userJpaRepository;
        this.customerJpaRepository = customerJpaRepository;
        this.attachmentJpaRepository = attachmentJpaRepository;
        this.companyJPARepository = companyJPARepository;
    }

    @Override
    public Optional<UserEntity> findUserByUsername(String userName) {
        return userJpaRepository.findByEmail(userName);
    }

    public List<Maintainer> findAvailableMaintainersForSlotAndCompany(String preferredSlot,Long id){
        return userJpaRepository.findAvailableMaintainersForSlotAndCompany(preferredSlot,id).stream()
                .map(MaintainerMapper::toDomainMaintainer)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Transactional
    public com.fixora.maintainance.user.domain.model.User addUser(MaintainerRequest maintainerRequest){
        UserEntity userEntity =new UserEntity();
        userEntity.setName(maintainerRequest.getName());
        Company company=new Company();
        company.setId(maintainerRequest.getCompanyId());
        userEntity.setCompany(company);
        userEntity.setEmail(maintainerRequest.getEmail());
        userEntity.setPhone(maintainerRequest.getPhone());
        userEntity.setRole(maintainerRequest.getRole());
        userJpaRepository.save(userEntity);

        return UserMapper.toDomain(userEntity);

    }

    @Transactional
    public com.fixora.maintainance.user.domain.model.User addUser(String name, String email, String phone, String role, Long companyId){
        return addUserWithStatus(name, email, phone, role, companyId, "ACTIVE");
    }

    @Transactional
    public com.fixora.maintainance.user.domain.model.User addUserWithStatus(String name, String email, String phone, String role, Long companyId, String status){
        UserEntity userEntity =new UserEntity();
        userEntity.setName(name);
        Company company=new Company();
        company.setId(companyId);
        userEntity.setCompany(company);
        userEntity.setEmail(email);
        userEntity.setPhone(phone);
        userEntity.setRole(role);
        userEntity.setStatus(status);
        userEntity.setPasswordHash(""); // Will be set when user confirms account
        userJpaRepository.save(userEntity);

        return UserMapper.toDomain(userEntity);
    }

    @Override
    public List<InactiveUser> findInactiveUsers() {
        List<UserEntity> inactiveUserEntities = userJpaRepository.findByStatus("INACTIVE");
        return inactiveUserEntities.stream()
                .filter(user -> "CUSTOMER".equals(user.getRole())) // Only customers can self-register
                .map(this::toInactiveUser)
                .collect(Collectors.toList());
    }

    @Override
    public InactiveUser findInactiveUserById(Long userId) {
        Optional<UserEntity> userEntityOpt = userJpaRepository.findByIdAndStatus(userId, "INACTIVE");
        if (userEntityOpt.isEmpty()) {
            return null;
        }
        UserEntity userEntity = userEntityOpt.get();
        if (!"CUSTOMER".equals(userEntity.getRole())) {
            return null;
        }
        return toInactiveUser(userEntity);
    }

    @Override
    @Transactional
    public void activateUser(Long userId) {
        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        userEntity.setStatus("ACTIVE");
        // Password will be set by user when they use the activation code
        userJpaRepository.save(userEntity);
    }

    @Override
    public Optional<com.fixora.maintainance.user.domain.model.User> findUserById(Long userId) {
        return userJpaRepository.findById(userId)
                .map(UserMapper::toDomain);
    }

    private InactiveUser toInactiveUser(UserEntity userEntity) {
        // Find customer record
        Optional<Customer> customerOpt = customerJpaRepository.findById(userEntity.getId());
        
        Long apartmentId = null;
        String apartmentNumber = null;
        Long buildingId = null;
        String buildingName = null;
        
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            if (customer.getApartment() != null) {
                apartmentId = customer.getApartment().getId();
                apartmentNumber = customer.getApartment().getApartmentNumber();
                
                if (customer.getApartment().getBuilding() != null) {
                    buildingId = customer.getApartment().getBuilding().getId();
                    buildingName = customer.getApartment().getBuilding().getName();
                }
            }
        }
        
        // Get company name
        String companyName = null;
        if (userEntity.getCompany() != null) {
            Optional<Company> companyOpt = companyJPARepository.findById(userEntity.getCompany().getId());
            if (companyOpt.isPresent()) {
                companyName = companyOpt.get().getName();
            }
        }
        
        // Get attachments
        List<CustomerAttachment> attachmentEntities = attachmentJpaRepository.findByUserId(userEntity.getId());
        List<Attachment> attachments = attachmentEntities.stream()
                .map(this::toAttachmentDomain)
                .collect(Collectors.toList());
        
        return InactiveUser.builder()
                .userId(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .companyId(userEntity.getCompany() != null ? userEntity.getCompany().getId() : null)
                .companyName(companyName)
                .buildingId(buildingId)
                .buildingName(buildingName)
                .apartmentId(apartmentId)
                .apartmentNumber(apartmentNumber)
                .registeredAt(userEntity.getCreatedAt())
                .attachments(attachments)
                .build();
    }

    private Attachment toAttachmentDomain(CustomerAttachment entity) {
        return Attachment.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .fileName(entity.getFileName())
                .fileUrl(entity.getFileUrl())
                .fileType(entity.getFileType())
                .attachmentType(entity.getAttachmentType())
                .build();
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, String encodedPassword) {
        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        userEntity.setPasswordHash(encodedPassword);
        userJpaRepository.save(userEntity);
    }

    @Override
    @Transactional
    public void updatePhone(Long userId, String phone) {
        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        userEntity.setPhone(phone);
        userJpaRepository.save(userEntity);
    }

}
