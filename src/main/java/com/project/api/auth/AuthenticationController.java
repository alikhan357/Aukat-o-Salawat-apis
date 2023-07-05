package com.project.api.auth;

import com.project.api.dto.request.AuthenticationRequest;
import com.project.api.dto.request.RegisterRequest;
import com.project.api.dto.response.AuthenticationResponse;
import com.project.api.dto.response.ServiceResponse;
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
    public ResponseEntity<ServiceResponse> register(@RequestBody RegisterRequest request){
        LOGGER.info("SignUp method called. Request {}",request);
        ServiceResponse response = service.register(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<ServiceResponse> authenticate(@RequestBody AuthenticationRequest request){
        LOGGER.info("authenticate method called. Request {}",request);
        ServiceResponse response = service.authenticate(request);
        return ResponseEntity.status(response.getCode()).body(response);

    }
}
