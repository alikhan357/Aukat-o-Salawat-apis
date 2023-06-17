package com.project.api.Controller;

import com.project.api.auth.AuthenticationController;
import com.project.api.dto.request.NamazTimeRequest;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.service.NamazService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/namaz")
@RequiredArgsConstructor
public class NamazController {

    private final NamazService namazService;

    private static final Logger LOGGER = LoggerFactory.getLogger(NamazController.class);

    @GetMapping("/methods")
    public ResponseEntity<ServiceResponse> getNamazMethods(Principal principal){

        LOGGER.info("getNamazMethods called by {} ",principal.getName());
        ServiceResponse response = namazService.getNamazMethods(principal);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/time")
    public ResponseEntity<ServiceResponse> getNamazTime(@RequestBody NamazTimeRequest request,Principal principal){
        LOGGER.info("getNamazTime called {} by {}",request,principal.getName());
        ServiceResponse response = namazService.getNamazTimings(request,principal);
        return ResponseEntity.status(response.getCode()).body(response);
    }

}

