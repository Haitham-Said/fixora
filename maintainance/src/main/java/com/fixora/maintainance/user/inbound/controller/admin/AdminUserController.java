package com.fixora.maintainance.user.inbound.controller.admin;

import com.fixora.maintainance.user.application.AdminUserApplicationService;
import com.fixora.maintainance.user.inbound.model.InactiveUserResponseDTO;
import com.fixora.maintainance.user.inbound.model.UserActivationRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin controller for managing user registrations and activations.
 * All endpoints require ADMIN authority.
 */
@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminUserController {

    private final AdminUserApplicationService adminUserApplicationService;

    public AdminUserController(AdminUserApplicationService adminUserApplicationService) {
        this.adminUserApplicationService = adminUserApplicationService;
    }

    /**
     * Get all inactive users (self-registered customers pending approval)
     * @return List of inactive users with their attachments
     */
    @GetMapping("/inactive")
    public ResponseEntity<List<InactiveUserResponseDTO>> getInactiveUsers() {
        List<InactiveUserResponseDTO> inactiveUsers = adminUserApplicationService.getInactiveUsers();
        return ResponseEntity.ok(inactiveUsers);
    }

    /**
     * Get a specific inactive user by ID with attachments
     * @param userId The user ID
     * @return Inactive user details with attachments
     */
    @GetMapping("/inactive/{userId}")
    public ResponseEntity<InactiveUserResponseDTO> getInactiveUser(@PathVariable Long userId) {
        InactiveUserResponseDTO inactiveUser = adminUserApplicationService.getInactiveUserById(userId);
        return ResponseEntity.ok(inactiveUser);
    }

    /**
     * Activate/approve a user after reviewing their registration and attachments
     * Sets the user status to ACTIVE and assigns a password
     * @param activationRequest User ID and password for activation
     * @return Success response
     */
    @PostMapping("/activate")
    public ResponseEntity<Void> activateUser(@Valid @RequestBody UserActivationRequestDTO activationRequest) {
        adminUserApplicationService.activateUser(activationRequest);
        return ResponseEntity.ok().build();
    }
}

