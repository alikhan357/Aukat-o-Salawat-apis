package com.project.api.auth;

import com.project.api.dto.request.AuthenticationRequest;
import com.project.api.dto.request.RegisterRequest;
import com.project.api.dto.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/signup")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){

        LOGGER.info("SignUp method called. Request {}",request);
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        LOGGER.info("authenticate method called. Request {}",request);
        return ResponseEntity.ok(service.authenticate(request));

    }
}
