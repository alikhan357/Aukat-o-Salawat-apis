package com.project.api.Controller;

import com.project.api.dto.request.PasswordRequest;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.service.DeviceService;
import com.project.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping("/validate/{deviceId}")
    public ResponseEntity<ServiceResponse> validate(@PathVariable String deviceId) {
        ServiceResponse resp = deviceService.validateDevice(deviceId);
        return ResponseEntity.status(resp.getCode()).body(resp);
    }

}
