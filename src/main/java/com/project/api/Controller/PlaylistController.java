package com.project.api.Controller;

import com.project.api.dto.response.ServiceResponse;
import com.project.api.model.Reminder;
import com.project.api.service.PlaylistService;
import com.project.api.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/playlist")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping("/save")
    public ResponseEntity<ServiceResponse> save(@RequestParam("file") MultipartFile file, Principal principal){
        ServiceResponse response = playlistService.save(file,principal);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/")
    public ResponseEntity<ServiceResponse> get(Principal principal){
        ServiceResponse response = playlistService.getByEmail(principal);
        return ResponseEntity.status(response.getCode()).body(response);
    }


}

