package com.fixora.security.inbound;

import com.fixora.security.inbound.model.AuthenticationRequest;
import com.fixora.security.inbound.model.AuthenticationResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/auth")
public class AuthenticationController {

    @PostMapping(value = "/login")

    public AuthenticationResponse AuthenticateUser(@RequestBody AuthenticationRequest loginRequest){

    }
}
