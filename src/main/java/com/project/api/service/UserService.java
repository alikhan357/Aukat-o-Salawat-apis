package com.project.api.service;

import com.project.api.config.JwtService;
import com.project.api.dto.request.AuthenticationRequest;
import com.project.api.dto.request.PasswordRequest;
import com.project.api.dto.request.RegisterRequest;
import com.project.api.dto.response.AuthenticationResponse;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.dto.response.UserResponse;
import com.project.api.model.User;
import com.project.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    public ServiceResponse getUserByEmail(String email) {

        try {
            Optional<User> user = userRepository.findByEmail(email);

            if(user.isPresent()) {
                UserResponse response = UserResponse.builder()
                        .firstName(user.get().getFirstName())
                        .lastname(user.get().getLastname())
                        .email(user.get().getEmail())
                        .macAddress(user.get().getMacAddress())
                        .build();
                return new ServiceResponse(HttpStatus.OK,"SUCCESS",response);
            }

            else return new ServiceResponse(HttpStatus.BAD_REQUEST,"User Not Found",null);
        } catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage(),null);
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

                return new ServiceResponse(HttpStatus.OK,"Password Updated Successfully",null);
            }
            else return new ServiceResponse(HttpStatus.BAD_REQUEST,"User Not Found",null);
        } catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage(),null);
        }

    }

}
