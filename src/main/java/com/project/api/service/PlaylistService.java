package com.project.api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.project.api.dto.response.AudioDTO;
import com.project.api.dto.response.PlaylistDTO;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.model.Audio;
import com.project.api.model.Playlist;
import com.project.api.repository.PlaylistRepository;
import com.project.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private final CloudUploadService uploadService;



    @Value("${aws.s3.size}")
    private Integer sizeLimit;

    @Value("${aws.s3.frequency}")
    private Integer fileFrequency;

    private static final Logger LOGGER = LoggerFactory.getLogger(PlaylistService.class);

    public ServiceResponse save(MultipartFile file, Principal principal) {
        try {
            // Validate file size
            if (file.getSize() > sizeLimit * 1000) {
                return new ServiceResponse(HttpStatus.BAD_REQUEST.value(),
                        "File Size cannot be greater than " + sizeLimit + " KB", null);
            }

            String fileName = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf('.'));
            String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1);

            if (!ext.equals("mp3")) {
                return new ServiceResponse(HttpStatus.BAD_REQUEST.value(), "Only mp3 files allowed", null);
            }

            String audioId = UUID.randomUUID().toString();
            fileName = fileName + "_" + audioId + "." + ext;

            // Set file metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());

            // Save file in cloud
            String URL = uploadService.uploadFile(fileName, file.getInputStream(), metadata);

            // Save file state in playlist
            Optional<Playlist> playlist = playlistRepository.findByEmail(principal.getName());
            Playlist userPlaylist;

            if (playlist.isPresent()) {
                userPlaylist = playlist.get();

                if (userPlaylist.getAudios().size() + 1 > fileFrequency) {
                    return new ServiceResponse(HttpStatus.BAD_REQUEST.value(),
                            "Only " + fileFrequency + " files are allowed per user", null);
                }

                userPlaylist.setUpdatedDate(new Date().toString());
                userPlaylist.getAudios().add(getAudio(file, fileName, audioId,URL));
            } else {
                // Create playlist
                userPlaylist = new Playlist();
                userPlaylist.setEmail(principal.getName());
                userPlaylist.setCreatedDate(new Date().toString());
                userPlaylist.setUpdatedDate(new Date().toString());

                Audio audio = getAudio(file, fileName, audioId,URL);
                userPlaylist.setAudios(List.of(audio));
            }

            playlistRepository.save(userPlaylist);

            LOGGER.info("File saved in playlist for user: {}", principal.getName());
            return new ServiceResponse(HttpStatus.OK.value(), "Success", null);
        } catch (Exception ex) {
            LOGGER.error("Error occurred while saving file in playlist: {}", ex.getMessage());
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), null);
        }
    }

    private Audio getAudio(MultipartFile file, String fileName, String id,String URL) {
        Audio audio = new Audio();
        audio.setId(id);
        audio.setName(fileName);
        audio.setSize(file.getSize());
        audio.setUrl(URL);
        audio.setCreatedDate(new Date().toString());
        audio.setUpdatedDate(new Date().toString());
        return audio;
    }

    public ServiceResponse getByEmail(Principal principal) {
        try {
            Optional<Playlist> playlist = playlistRepository.findByEmail(principal.getName());
            if (playlist.isPresent()) {
                PlaylistDTO playlistDTO = modelMapper.map(playlist.get(), PlaylistDTO.class);
                LOGGER.info("Retrieved playlist for user: {}", principal.getName());
                return new ServiceResponse(HttpStatus.OK.value(), "SUCCESS", playlistDTO);
            } else {
                LOGGER.warn("User does not have a playlist: {}", principal.getName());
                return new ServiceResponse(HttpStatus.NOT_FOUND.value(), "User does not have a playlist", null);
            }
        } catch (Exception ex) {
            LOGGER.error("Error occurred while retrieving playlist: {}", ex.getMessage());
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), null);
        }
    }

    public ServiceResponse markAudioAsFav(Principal principal, String id) {
        try {
            Optional<Playlist> playlist = playlistRepository.findByEmail(principal.getName());
            if (playlist.isPresent()) {
                List<Audio> audios = playlist.get().getAudios();
                boolean isAudioFav = false;
                for (Audio audio : audios) {
                    if (audio.getId().equals(id)) {
                        audio.setIsFav(true);
                        isAudioFav = true;
                        break;
                    }
                }

                if (!isAudioFav) {
                    LOGGER.warn("Audio with ID '{}' not found in user's playlist: {}", id, principal.getName());
                    return new ServiceResponse(HttpStatus.NOT_FOUND.value(), "Audio not found in playlist", null);
                }

                playlistRepository.save(playlist.get());
                LOGGER.info("Marked audio as favorite for user: {}", principal.getName());
                return new ServiceResponse(HttpStatus.OK.value(), "SUCCESS", null);
            } else {
                LOGGER.warn("User does not have a playlist: {}", principal.getName());
                return new ServiceResponse(HttpStatus.NOT_FOUND.value(), "User does not have a playlist", null);
            }
        } catch (Exception ex) {
            LOGGER.error("Error occurred while marking audio as favorite: {}", ex.getMessage());
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), null);
        }
    }

    public ServiceResponse markAudioAsFav(Principal principal, List<String> ids) {
        try {
            Optional<Playlist> playlist = playlistRepository.findByEmail(principal.getName());
            if (playlist.isPresent()) {
                List<Audio> audios = playlist.get().getAudios();
                boolean isAnyAudioFound = false;

                for (Audio audio : audios) {
                    audio.setIsFav(false);
                    if (ids.contains(audio.getId())) {
                        audio.setIsFav(true);
                        isAnyAudioFound = true;
                    }
                }

                if (!isAnyAudioFound) {
                    LOGGER.warn("Audios with provided IDs not found in user's playlist: {}", ids);
                    return new ServiceResponse(HttpStatus.NOT_FOUND.value(), "Audios not found in playlist", null);
                }

                playlistRepository.save(playlist.get());
                LOGGER.info("Marked audios as favorites for user: {}", principal.getName());
                return new ServiceResponse(HttpStatus.OK.value(), "SUCCESS", null);
            } else {
                LOGGER.warn("User does not have a playlist: {}", principal.getName());
                return new ServiceResponse(HttpStatus.NOT_FOUND.value(), "User does not have a playlist", null);
            }
        } catch (Exception ex) {
            LOGGER.error("Error occurred while marking audios as favorites: {}", ex.getMessage());
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), null);
        }
    }

    public ServiceResponse getFavAudio(Principal principal) {
        try {
            Optional<Playlist> playlist = playlistRepository.findByEmail(principal.getName());
            if (playlist.isPresent()) {
                List<Audio> favAudios = playlist.get().getAudios().stream()
                        .filter(Audio::getIsFav)
                        .collect(Collectors.toList());
                List<AudioDTO> favAudioDTOs = modelMapper.map(favAudios, new TypeToken<List<AudioDTO>>() {}.getType());
                LOGGER.info("Retrieved favorite audios for user: {}", principal.getName());
                return new ServiceResponse(HttpStatus.OK.value(), "SUCCESS", favAudioDTOs);
            } else {
                LOGGER.warn("User does not have a playlist: {}", principal.getName());
                return new ServiceResponse(HttpStatus.NOT_FOUND.value(), "User does not have a playlist", null);
            }
        } catch (Exception ex) {
            LOGGER.error("Error occurred while retrieving favorite audios: {}", ex.getMessage());
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), null);
        }
    }
}
