package com.project.api.service;

import com.project.api.dto.request.PasswordRequest;
import com.project.api.dto.response.MethodsDTO;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.dto.response.UserResponse;
import com.project.api.helper.Constants;
import com.project.api.model.Method;
import com.project.api.model.School;
import com.project.api.model.User;
import com.project.api.repository.UserRepository;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

    @Value("${timezone.url}")
    private String timeZoneUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    public ServiceResponse getUserByEmail(String email) {
        try {
            Optional<User> user = userRepository.findByEmail(email);

            if (user.isPresent()) {
                UserResponse response = UserResponse.builder()
                        .firstName(user.get().getFirstName())
                        .lastname(user.get().getLastname())
                        .email(user.get().getEmail())
                        .serialNumber(user.get().getSerialNumber())
                        .method(user.get().getMethod() == null ? null : this.modelMapper.map(user.get().getMethod(), MethodsDTO.class))
                        .school(user.get().getSchool() == null ? null : this.modelMapper.map(user.get().getSchool(), MethodsDTO.class))
                        .lat(user.get().getLat())
                        .lng(user.get().getLng())
                        .timeZone(user.get().getTimeZone())
                        .build();
                return new ServiceResponse(HttpStatus.OK.value(), "SUCCESS", response);
            } else {
                return new ServiceResponse(HttpStatus.BAD_REQUEST.value(), "User Not Found", null);
            }
        } catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    public ServiceResponse updatePassword(PasswordRequest request) {
        try {
            Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setPassword(encoder.encode(request.getPassword()));
                userRepository.update(user);

                return new ServiceResponse(HttpStatus.OK.value(), "Password Updated Successfully", null);
            } else {
                return new ServiceResponse(HttpStatus.BAD_REQUEST.value(), "User Not Found", null);
            }
        } catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    public ServiceResponse updateMethod(Integer methodId, Integer schoolId, Principal principal) {
        try {
            Optional<User> optionalUser = userRepository.findByEmail(principal.getName());

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setMethod(new Method(methodId, Constants.methods.get(methodId)));
                user.setSchool(new School(schoolId, schoolId == 0 ? "Shafi" : "Hanafi"));
                userRepository.save(user);

                return new ServiceResponse(HttpStatus.OK.value(), "Method Updated Successfully", null);
            } else {
                return new ServiceResponse(HttpStatus.BAD_REQUEST.value(), "User Not Found", null);
            }
        } catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    public ServiceResponse updateLocation(Double lat, Double lng, Principal principal) {
        try {
            Optional<User> optionalUser = userRepository.findByEmail(principal.getName());

            if (optionalUser.isPresent()) {

                String timeZone = getTimeZone(lat,lng);

                User user = optionalUser.get();
                user.setLat(lat);
                user.setLng(lng);
                user.setTimeZone(timeZone);

                userRepository.save(user);

                return new ServiceResponse(HttpStatus.OK.value(), "Location Updated Successfully", timeZone);
            } else {
                return new ServiceResponse(HttpStatus.BAD_REQUEST.value(), "User Not Found", null);
            }
        } catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    public ServiceResponse updateSerialNumber(String serialNumber, Principal principal) {
        try {
            Optional<User> optionalUser = userRepository.findByEmail(principal.getName());

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setSerialNumber(serialNumber);
                userRepository.save(user);

                return new ServiceResponse(HttpStatus.OK.value(), "Serial Updated Successfully", null);
            } else {
                return new ServiceResponse(HttpStatus.BAD_REQUEST.value(), "User Not Found", null);
            }
        } catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    public ServiceResponse updateTimezone(String timeZone, Principal principal) {
        try {
            Optional<User> optionalUser = userRepository.findByEmail(principal.getName());

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setTimeZone(timeZone);
                userRepository.save(user);

                return new ServiceResponse(HttpStatus.OK.value(), "Timezone Updated Successfully", null);
            } else {
                return new ServiceResponse(HttpStatus.BAD_REQUEST.value(), "User Not Found", null);
            }
        } catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    private String getTimeZone(Double lat, Double lng) {
        HttpResponse<JsonNode> response = Unirest.get(timeZoneUrl + String.format("/%f,%f",lat,lng)).asJson();

        LOGGER.info("Response from timezone API. STATUS: {}",response.getStatus());
        LOGGER.info("Response from timezone API. BODY: {}",response.getBody().toString());

        return response.isSuccess() ? response.getBody().getObject().getString("timezone_id") : null;
    }
}
