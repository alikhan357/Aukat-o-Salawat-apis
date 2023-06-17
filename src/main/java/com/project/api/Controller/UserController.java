package com.project.api.Controller;

import com.project.api.dto.request.PasswordRequest;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.dto.response.UserResponse;
import com.project.api.model.User;
import com.project.api.repository.UserRepository;
import com.project.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/byEmail/{email}")
    public ResponseEntity<ServiceResponse> getUserByEmail(@PathVariable String email) {
        LOGGER.info("getUserByEmail called {} ",email);
        ServiceResponse resp = userService.getUserByEmail(email);
        return ResponseEntity.status(resp.getCode()).body(resp);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ServiceResponse> updatePassword(@RequestBody PasswordRequest request){
        LOGGER.info("updatePassword called {} ",request);
        ServiceResponse resp = userService.updatePassword(request);
        return ResponseEntity.status(resp.getCode()).body(resp);
    }

    @PutMapping("/method")
    public ResponseEntity<ServiceResponse> updateMethod(@RequestParam("method") Integer method, @RequestParam("school") Integer school, Principal principal){
        LOGGER.info("updateMethod called method: {}, school: {} by {} ",method,school,principal.getName());
        ServiceResponse resp = userService.updateMethod(method,school,principal);
        return ResponseEntity.status(resp.getCode()).body(resp);
    }

    @PutMapping("/location")
    public ResponseEntity<ServiceResponse> updateLocation(@RequestParam("lat") Double lat, @RequestParam("lng") Double lng, Principal principal){
        LOGGER.info("updateLocation called lat: {}, long: {} by {} ",lat,lng,principal.getName());
        ServiceResponse resp = userService.updateLocation(lat,lng,principal);
        return ResponseEntity.status(resp.getCode()).body(resp);
    }

    @PutMapping("/serialnumber")
    public ResponseEntity<ServiceResponse> updateSerial(@RequestParam("serial") String serial,Principal principal){
        LOGGER.info("updateSerial called serial: {} by {} ",serial,principal.getName());
        ServiceResponse resp = userService.updateSerialNumber(serial,principal);
        return ResponseEntity.status(resp.getCode()).body(resp);
    }

    @PutMapping("/timezone")
    public ResponseEntity<ServiceResponse> updateTimeZone(@RequestParam("timeZone") String timeZone,Principal principal){
        LOGGER.info("updateTimeZone called stimeZoneerial: {} by {} ",timeZone,principal.getName());
        ServiceResponse resp = userService.updateTimezone(timeZone,principal);
        return ResponseEntity.status(resp.getCode()).body(resp);
    }

}
