package com.fixora.security.application.service;


import com.fixora.maintainance.user.domain.entity.UserEntity;
import com.fixora.maintainance.user.domain.exception.InvalidCredentialException;
import com.fixora.maintainance.user.domain.service.UserService;
import com.fixora.security.inbound.model.AuthenticationRequest;
import com.fixora.security.inbound.model.AuthenticationResponse;
import com.fixora.security.infrastructure.util.JWTUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class AuthenticationService {

    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserService userService, JWTUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthenticationResponse authenticateUser(AuthenticationRequest loginRequest) {
        UserEntity user = userService.findUserByEmail(loginRequest.userName());
        if(!passwordEncoder.matches(loginRequest.password(),user.getPassword())){
            throw new InvalidCredentialException("Invalid Credentials");
        }
        String token = jwtUtil.generateToken(user.getEmail(),buildClaims(user));
        return new AuthenticationResponse(token);
    }

    public Map<String, Object> buildClaims(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("userEmail",user.getEmail());
        claims.put("role", user.getRole());
        claims.put("companyId",user.getCompanyId());
        return claims;
    }
}
