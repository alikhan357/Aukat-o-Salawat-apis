package com.project.api.Controller;

import com.project.api.dto.request.PasswordRequest;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
public class OTPController {


    @PostMapping("/validate")
    public ResponseEntity<ServiceResponse> validate(@RequestBody Map<String,String> otp) {
        return ResponseEntity.ok(new ServiceResponse(HttpStatus.OK,"SUCCESS",null));
    }

    @PostMapping("/generate")
    public ResponseEntity<ServiceResponse> generate(@RequestBody Map<String,String> email) {
        return ResponseEntity.ok(new ServiceResponse(HttpStatus.OK,"SUCCESS",null));
    }


}
