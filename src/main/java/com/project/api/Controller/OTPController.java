package com.project.api.Controller;

import com.project.api.dto.request.AlexaCodeRequest;
import com.project.api.dto.request.OTPGenRequest;
import com.project.api.dto.request.PasswordRequest;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.service.OTPService;
import com.project.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
public class OTPController {

    @Autowired
    OTPService otpService;

    private static final Logger LOGGER = LoggerFactory.getLogger(NamazController.class);
    @PostMapping("/validate")
    public ResponseEntity<ServiceResponse> validate(@RequestBody Map<String,String> otp) {
        return ResponseEntity.ok(new ServiceResponse(HttpStatus.OK.value(),"CODE VALIDATED",null));
    }

    @PostMapping("/generate")
    public ResponseEntity<ServiceResponse> generate(@RequestBody OTPGenRequest request) {
        LOGGER.info("generate called {}",request);
        ServiceResponse response = otpService.generateOTP(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/alexa/generate")
    public ResponseEntity<ServiceResponse> generateAlexaCode(Principal principal) {
        LOGGER.info("generateAlexaCode called by {}",principal.getName());
        ServiceResponse response = otpService.generateAlexaCode(principal.getName());
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/alexa/validate")
            public ResponseEntity<ServiceResponse> validateAlexaCode(@RequestBody AlexaCodeRequest request) {
        LOGGER.info("validateAlexaCode called {}",request);
        ServiceResponse response = otpService.validateAlexaCode(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }


}
