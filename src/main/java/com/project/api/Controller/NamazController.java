package com.project.api.Controller;

import com.project.api.dto.request.NamazTimeRequest;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.service.NamazService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/namaz")
@RequiredArgsConstructor
public class NamazController {

    private final NamazService namazService;

    @GetMapping("/methods")
    public ResponseEntity<ServiceResponse> getNamazMethods(){
        ServiceResponse response = namazService.getNamazMethods();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/time")
    public ResponseEntity<ServiceResponse> getNamazTime(@RequestBody NamazTimeRequest request){
        ServiceResponse response = namazService.getNamazTimings(request);
        return ResponseEntity.status(response.getCode()).body(response);
    }

}

