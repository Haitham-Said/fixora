package com.fixora.security.inbound;

import com.fixora.security.application.service.AuthenticationService;
import com.fixora.security.inbound.model.AuthenticationRequest;
import com.fixora.security.inbound.model.AuthenticationResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Portal staff login only. Tenants and maintainers use WhatsApp in MVP — no mobile app login.
 */
@RestController()
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping(value = "/login")
    public AuthenticationResponse AuthenticateUser(@Valid @RequestBody AuthenticationRequest loginRequest){
        return authenticationService.authenticateUser(loginRequest);
    }
}
