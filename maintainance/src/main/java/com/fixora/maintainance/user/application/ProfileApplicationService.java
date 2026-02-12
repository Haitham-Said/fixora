package com.fixora.maintainance.user.application;

import com.fixora.maintainance.user.domain.repositories.IUserRepository;
import com.fixora.maintainance.user.inbound.model.ProfileUpdateRequestDTO;
import com.fixora.security.application.model.UserInfo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for user profile management
 * Handles profile completion after first-time login with code
 */
@Service
public class ProfileApplicationService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileApplicationService(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Completes user profile by setting password and optionally updating phone
     * This should be called after first-time login with activation code
     */
    @Transactional
    public void completeProfile(UserInfo userInfo, ProfileUpdateRequestDTO profileUpdate) {
        // Update password
        String encodedPassword = passwordEncoder.encode(profileUpdate.password());
        userRepository.updatePassword(userInfo.userId(), encodedPassword);
        
        // Update phone if provided
        if (profileUpdate.phone() != null && !profileUpdate.phone().isEmpty()) {
            userRepository.updatePhone(userInfo.userId(), profileUpdate.phone());
        }
    }
}

