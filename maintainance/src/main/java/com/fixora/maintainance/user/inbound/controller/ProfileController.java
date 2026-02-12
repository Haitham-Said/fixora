package com.fixora.maintainance.user.inbound.controller;

import com.fixora.maintainance.user.application.ProfileApplicationService;
import com.fixora.maintainance.user.inbound.model.ProfileUpdateRequestDTO;
import com.fixora.security.application.model.UserInfo;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user profile management
 * Allows users to complete their profile after first-time login
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileApplicationService profileApplicationService;

    public ProfileController(ProfileApplicationService profileApplicationService) {
        this.profileApplicationService = profileApplicationService;
    }

    @PutMapping("/complete")
    public ResponseEntity<Void> completeProfile(
            @AuthenticationPrincipal UserInfo userInfo,
            @Valid @RequestBody ProfileUpdateRequestDTO profileUpdate) {
        profileApplicationService.completeProfile(userInfo, profileUpdate);
        return ResponseEntity.ok().build();
    }
}

