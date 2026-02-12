package com.fixora.security.application.service;


import com.fixora.maintainance.user.domain.model.UserCode;
import com.fixora.maintainance.user.domain.repositories.IUserCodeRepository;
import com.fixora.maintainance.user.infrastructure.entity.UserEntity;
import com.fixora.maintainance.user.domain.exception.InvalidCodeException;
import com.fixora.maintainance.user.domain.exception.InvalidCredentialException;
import com.fixora.maintainance.user.domain.service.UserService;
import com.fixora.security.inbound.model.AuthenticationRequest;
import com.fixora.security.inbound.model.AuthenticationResponse;
import com.fixora.security.inbound.model.CodeAuthenticationRequest;
import com.fixora.security.infrastructure.util.JWTUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Service
public class AuthenticationService {

    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final IUserCodeRepository userCodeRepository;

    public AuthenticationService(UserService userService, JWTUtil jwtUtil, PasswordEncoder passwordEncoder,
                                IUserCodeRepository userCodeRepository) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userCodeRepository = userCodeRepository;
    }

    public AuthenticationResponse authenticateUser(AuthenticationRequest loginRequest) {
        UserEntity userEntity = userService.findUserByEmail(loginRequest.userName());
        if(!passwordEncoder.matches(loginRequest.password(), userEntity.getPasswordHash())){
            throw new InvalidCredentialException("Invalid Credentials");
        }
        String token = jwtUtil.generateToken(userEntity.getEmail(),buildClaims(userEntity));
        return new AuthenticationResponse(token);
    }

    /**
     * Authenticates a user using a code (first-time login)
     * Validates the code, checks if it's not expired or already used, then marks it as used
     */
    public AuthenticationResponse authenticateWithCode(CodeAuthenticationRequest codeRequest) {
        // Find user by email
        UserEntity userEntity = userService.findUserByEmail(codeRequest.email());
        
        // Find code by code string
        UserCode userCode = userCodeRepository.findByCode(codeRequest.code())
                .orElseThrow(() -> new InvalidCodeException("Invalid or expired code"));
        
        // Validate code belongs to user
        if (!userCode.getUserId().equals(userEntity.getId())) {
            throw new InvalidCodeException("Code does not belong to this user");
        }
        
        // Check if code is already used
        if (Boolean.TRUE.equals(userCode.getIsUsed())) {
            throw new InvalidCodeException("Code has already been used");
        }
        
        // Check if code is expired
        if (userCode.getExpiresAt() != null && userCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidCodeException("Code has expired");
        }
        
        // Check if code status is ACTIVE (user must be activated)
        if (!"ACTIVE".equals(userCode.getStatus())) {
            throw new InvalidCodeException("Code is not active. Please wait for account activation.");
        }
        
        // Mark code as used
        userCodeRepository.markCodeAsUsed(codeRequest.code());
        
        // Generate JWT token
        String token = jwtUtil.generateToken(userEntity.getEmail(), buildClaims(userEntity));
        return new AuthenticationResponse(token);
    }

    public Map<String, Object> buildClaims(UserEntity userEntity) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", (Long) userEntity.getId());
        claims.put("userEmail", userEntity.getEmail());
        claims.put("role", userEntity.getRole());
        if(userEntity.getCompany()!=null)
            claims.put("companyId",(Long) userEntity.getCompany().getId());
        return claims;
    }
}
