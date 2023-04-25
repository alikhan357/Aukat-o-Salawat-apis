package com.project.api.Controller;

import com.project.api.dto.request.PasswordRequest;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.dto.response.UserResponse;
import com.project.api.model.User;
import com.project.api.repository.UserRepository;
import com.project.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/byEmail/{email}")
    public ResponseEntity<ServiceResponse> getUserByEmail(@PathVariable String email) {
        ServiceResponse resp = userService.getUserByEmail(email);
        return ResponseEntity.status(resp.getCode()).body(resp);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ServiceResponse> updatePassword(@RequestBody PasswordRequest request){
        ServiceResponse resp = userService.updatePassword(request);
        return ResponseEntity.status(resp.getCode()).body(resp);
    }

}
