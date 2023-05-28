package com.project.api.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.project.api.dto.response.AudioDTO;
import com.project.api.dto.response.PlaylistDTO;
import com.project.api.dto.response.ReminderDTO;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.model.Audio;
import com.project.api.model.Playlist;
import com.project.api.repository.PlaylistRepository;
import com.project.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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

    @Value("${aws.s3.bucket_name}")
    private String s3BucketName;

    @Value("${aws.s3.size}")
    private Integer sizeLimit;

    @Value("${aws.s3.frequency}")
    private Integer fileFrequency;

    private final AmazonS3 amazonS3;


    public ServiceResponse save(MultipartFile file, Principal principal){
        try{
            //validate file size
            if(file.getSize() > sizeLimit*1000){
                return new ServiceResponse(HttpStatus.BAD_REQUEST.value()
                        , "File Size cannot be greater than " + String.valueOf(sizeLimit) +" KB"
                        ,null);
            }

            String fileName = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf('.'));
            String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1);

            if(!ext.equals("mp3"))
                return new ServiceResponse(HttpStatus.BAD_REQUEST.value(), "Only mp3 files allowed",null);

            String audioId = UUID.randomUUID().toString();
            fileName = fileName + "_" + audioId + "." + ext;

            //set file metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());

            //save file in s3
            amazonS3.putObject(s3BucketName, fileName, file.getInputStream(), metadata);

            //save file state in playlist
            Optional<Playlist> playlist = playlistRepository.findByEmail(principal.getName());

            Playlist userPlaylist;

            if(playlist.isPresent()){
                userPlaylist = playlist.get();

                if(userPlaylist.getAudios().size() + 1 > fileFrequency)
                    return new ServiceResponse(HttpStatus.BAD_REQUEST.value(),
                            "Only " + fileFrequency +" files are allowed per user",null);

                userPlaylist.setUpdatedDate(new Date().toString());
                userPlaylist.getAudios().add(getAudio(file, fileName,audioId));

            }
            else{
                //create playlist
                userPlaylist = new Playlist();
                userPlaylist.setEmail(principal.getName());
                userPlaylist.setCreatedDate(new Date().toString());
                userPlaylist.setUpdatedDate(new Date().toString());

                Audio audio = getAudio(file, fileName,audioId);

                userPlaylist.setAudios(List.of(audio));

            }

            playlistRepository.save(userPlaylist);
        }
        catch (Exception ex){
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),ex.getMessage(),null);

        }

        return new ServiceResponse(HttpStatus.OK.value(),"Success",null);
    }

    private Audio getAudio(MultipartFile file, String fileName,String id) {
        Audio audio = new Audio();

        audio.setId(id);
        audio.setName(fileName);
        audio.setSize(file.getSize());
        audio.setCreatedDate(new Date().toString());
        audio.setUpdatedDate(new Date().toString());
        return audio;
    }

    public ServiceResponse getByEmail(Principal principal) {

        try {
            Optional<Playlist> playlist = playlistRepository.findByEmail(principal.getName());
            if(playlist.isPresent())
                return new ServiceResponse(HttpStatus.OK.value(), "SUCCESS",this.modelMapper.map(playlist.get(), PlaylistDTO.class));
            else
                return new ServiceResponse(HttpStatus.NOT_FOUND.value(),"User does not have a playlist",null);
        }
        catch (Exception ex){
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),ex.getMessage(),null);
        }

    }

    public ServiceResponse markAudioAsFav(Principal principal,String id){

        try {
            Optional<Playlist> playlist = playlistRepository.findByEmail(principal.getName());
            if(playlist.isPresent()) {
                 playlist.get().getAudios().forEach(audio -> {
                     if(audio.getId().equals(id)) audio.setIsFav(true);
                 });
                 playlistRepository.save(playlist.get());
                return new ServiceResponse(HttpStatus.OK.value(), "SUCCESS", null);
            }
            else
                return new ServiceResponse(HttpStatus.NOT_FOUND.value(),"User does not have a playlist",null);
        }
        catch (Exception ex){
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),ex.getMessage(),null);
        }
    }

    public ServiceResponse getFavAudio(Principal principal) {

        try {
            Optional<Playlist> playlist = playlistRepository.findByEmail(principal.getName());
            if(playlist.isPresent()) {
                List<Audio> audios =
                        playlist.get()
                                .getAudios().stream()
                                .filter(x -> x.getIsFav()).collect(Collectors.toList());

                return new ServiceResponse(HttpStatus.OK.value(), "SUCCESS",
                        modelMapper.map(audios, new TypeToken<List<AudioDTO>>(){}.getType()));
            }
            else
                return new ServiceResponse(HttpStatus.NOT_FOUND.value(),"User does not have a playlist",null);
        }
        catch (Exception ex){
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),ex.getMessage(),null);
        }

    }

}
