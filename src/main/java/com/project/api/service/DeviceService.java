package com.project.api.service;

import com.project.api.dto.request.PasswordRequest;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.dto.response.UserResponse;
import com.project.api.model.User;
import com.project.api.repository.UserDeviceRepository;
import com.project.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final UserDeviceRepository userDeviceRepository;



    public ServiceResponse validateDevice(String deviceId) {

        try{
           if(userDeviceRepository.findByDevice(deviceId).isPresent()){
               return new ServiceResponse(HttpStatus.OK.value(),"Success",null);
           }
           else{
               return new ServiceResponse(HttpStatus.NOT_FOUND.value(),"Data Not Found",null);
           }


        }
        catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage(),null);
        }
    }
}
