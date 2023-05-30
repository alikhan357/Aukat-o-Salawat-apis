package com.project.api.service;

import com.project.api.config.JwtService;
import com.project.api.dto.request.AuthenticationRequest;
import com.project.api.dto.request.PasswordRequest;
import com.project.api.dto.request.RegisterRequest;
import com.project.api.dto.response.AuthenticationResponse;
import com.project.api.dto.response.MethodsDTO;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.dto.response.UserResponse;
import com.project.api.helper.Constants;
import com.project.api.model.Method;
import com.project.api.model.School;
import com.project.api.model.User;
import com.project.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.http.protocol.HTTP;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final ModelMapper modelMapper;

    public ServiceResponse getUserByEmail(String email) {

        try {
            Optional<User> user = userRepository.findByEmail(email);

            if(user.isPresent()) {
                UserResponse response = UserResponse.builder()
                        .firstName(user.get().getFirstName())
                        .lastname(user.get().getLastname())
                        .email(user.get().getEmail())
                        .macAddress(user.get().getMacAddress())
                        .method(user.get().getMethod() == null ? null : this.modelMapper.map(user.get().getMethod(),MethodsDTO.class))
                        .school(user.get().getSchool() == null ? null :this.modelMapper.map(user.get().getSchool(),MethodsDTO.class))
                        .lat(user.get().getLat())
                        .lng(user.get().getLng())

                        .build();
                return new ServiceResponse(HttpStatus.OK.value(),"SUCCESS",response);
            }

            else return new ServiceResponse(HttpStatus.BAD_REQUEST.value(),"User Not Found",null);
        } catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage(),null);
        }
    }

    public ServiceResponse updatePassword(PasswordRequest request){

        try {
            Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

            if(optionalUser.isPresent()){
                //validate otp
                //validateOTP(request.getOtp())
                //update password
                User user = optionalUser.get();
                user.setPassword(encoder.encode(request.getPassword()));
                userRepository.update(user);

                return new ServiceResponse(HttpStatus.OK.value(),"Password Updated Successfully",null);
            }
            else return new ServiceResponse(HttpStatus.BAD_REQUEST.value(),"User Not Found",null);
        } catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage(),null);
        }

    }

    public ServiceResponse updateMethod(Integer methodId,Integer schoolId, Principal principal){

        try {
            Optional<User> optionalUser = userRepository.findByEmail(principal.getName());

            if(optionalUser.isPresent()){

                User user = optionalUser.get();

                user.setMethod(new Method(methodId,Constants.methods.get(methodId)));
                user.setSchool(new School(schoolId,schoolId == 0 ? "Shafi" : "Hanafi"));

                userRepository.save(user);

                return new ServiceResponse(HttpStatus.OK.value(),"Method Updated Successfully",null);
            }
            else return new ServiceResponse(HttpStatus.BAD_REQUEST.value(),"User Not Found",null);
        } catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage(),null);
        }

    }

    public ServiceResponse updateLocation(Double lat,Double lng, Principal principal){

        try {
            Optional<User> optionalUser = userRepository.findByEmail(principal.getName());

            if(optionalUser.isPresent()){

                User user = optionalUser.get();
                user.setLat(lat);
                user.setLng(lng);
                userRepository.save(user);

                return new ServiceResponse(HttpStatus.OK.value(),"Location Updated Successfully",null);
            }
            else return new ServiceResponse(HttpStatus.BAD_REQUEST.value(),"User Not Found",null);
        } catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage(),null);
        }

    }



}
