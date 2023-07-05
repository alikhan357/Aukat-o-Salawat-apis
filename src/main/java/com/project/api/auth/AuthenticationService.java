package com.project.api.auth;

import com.project.api.config.JwtService;
import com.project.api.dto.request.AuthenticationRequest;
import com.project.api.dto.request.RegisterRequest;
import com.project.api.dto.response.AuthenticationResponse;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.model.User;
import com.project.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);
    public ServiceResponse register(RegisterRequest request) {

        try {

            Optional<User> alreadyExist = userRepository.findByEmail(request.getEmail());

            if (!alreadyExist.isPresent()) {
                var user = User.builder()
                        .firstName(request.getFirstName())
                        .lastname(request.getLastname())
                        .email(request.getEmail())
//                .password(encoder.encode(request.getPassword()))
                        .build();

                userRepository.save(user);

                LOGGER.info("User {} Successfully registered", request.getEmail());

                return new ServiceResponse(HttpStatus.OK.value(), "SUCCESS", getAuthenticationResponse(user));
            } else {
                LOGGER.info("User {} Already Registered", request.getEmail());
                return new ServiceResponse(HttpStatus.BAD_REQUEST.value(), "Email Already Registered", null);
            }

        }

        catch (Exception ex){
            LOGGER.error("Error Occurred On Sign UP {}",ex.getMessage());
            ex.printStackTrace();
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unable to process request",null);
        }


    }
    public ServiceResponse authenticate(AuthenticationRequest request) {

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            var user = userRepository.findByEmail(request.getEmail());

            if (user.isPresent()) {
                LOGGER.info("{} authenticated ", user.get().getEmail());
                return new ServiceResponse(HttpStatus.OK.value(), "SUCCESS", getAuthenticationResponse(user.get()));
            } else
                return new ServiceResponse(HttpStatus.BAD_REQUEST.value(), "Invalid Username or Password", null);

        }
        catch (AuthenticationException ae){
            LOGGER.error("Authentication Exception for user {}",request.getEmail());
            LOGGER.error("Error Message {}",ae.getMessage());
            return new ServiceResponse(HttpStatus.BAD_REQUEST.value(), "Invalid Username or Password", null);
        }
        catch (Exception ex){
            LOGGER.error("Error Occurred On Login {}",ex.getMessage());
            ex.printStackTrace();
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unable to process request",null);
        }
    }


    private AuthenticationResponse getAuthenticationResponse(UserDetails user) {
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
