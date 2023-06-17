package com.project.api.Controller;

import com.project.api.dto.response.ServiceResponse;
import com.project.api.model.Reminder;
import com.project.api.service.PlaylistService;
import com.project.api.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(PlaylistController.class);

    @PostMapping("/save")
    public ResponseEntity<ServiceResponse> save(@RequestParam("file") MultipartFile file, Principal principal){
        LOGGER.info("save called {} by {}",file.getName(),principal.getName());
        ServiceResponse response = playlistService.save(file,principal);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/")
    public ResponseEntity<ServiceResponse> get(Principal principal){
        LOGGER.info("get called by {}",principal.getName());
        ServiceResponse response = playlistService.getByEmail(principal);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/audio/fav/{id}")
    public ResponseEntity<ServiceResponse> favoriteAudio(Principal principal,@PathVariable String id){
        LOGGER.info("favoriteAudio called {} by {}",id,principal.getName());
        ServiceResponse response = playlistService.markAudioAsFav(principal,id);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/audio/fav")
    public ResponseEntity<ServiceResponse> favoriteAudio(Principal principal,@RequestBody List<String> ids){
        LOGGER.info("favoriteAudio called {} by {}",ids,principal.getName());
        ServiceResponse response = playlistService.markAudioAsFav(principal,ids);
        return ResponseEntity.status(response.getCode()).body(response);
    }


    @GetMapping("/audio/fav")
    public ResponseEntity<ServiceResponse> favoriteAudio(Principal principal){
        LOGGER.info("GET favoriteAudio called by {}",principal.getName());
        ServiceResponse response = playlistService.getFavAudio(principal);
        return ResponseEntity.status(response.getCode()).body(response);
    }



}

