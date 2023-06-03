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
import java.util.List;

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

    @PostMapping("/audio/fav/{id}")
    public ResponseEntity<ServiceResponse> favoriteAudio(Principal principal,@PathVariable String id){
        ServiceResponse response = playlistService.markAudioAsFav(principal,id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/audio/fav")
    public ResponseEntity<ServiceResponse> favoriteAudio(Principal principal,@RequestBody List<String> ids){
        ServiceResponse response = playlistService.markAudioAsFav(principal,ids);
        return ResponseEntity.status(response.getCode()).body(response);
    }


    @GetMapping("/audio/fav")
    public ResponseEntity<ServiceResponse> favoriteAudio(Principal principal){
        ServiceResponse response = playlistService.getFavAudio(principal);
        return ResponseEntity.status(response.getCode()).body(response);
    }



}

