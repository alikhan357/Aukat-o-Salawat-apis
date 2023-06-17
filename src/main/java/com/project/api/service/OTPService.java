package com.project.api.service;

import com.project.api.dto.request.AlexaCodeRequest;
import com.project.api.dto.request.OTPGenRequest;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.model.User;
import com.project.api.model.UserDevice;
import com.project.api.repository.UserDeviceRepository;
import com.project.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OTPService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserDeviceRepository userDeviceRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(OTPService.class);
    private static final Map<String, String> otpMap = new HashMap<>();
    private static final Map<String, Date> otpExpiry = new HashMap<>();
    private static final Map<String, String> alexaMap = new HashMap<>();
    private static final Map<String, Date> alexaExpiry = new HashMap<>();

    public ServiceResponse generateOTP(OTPGenRequest request) {
        try {
            String otp = getOTP(5);
            otpMap.put(request.getEmail(), otp);

            Optional<User> user = userRepository.findByEmail(request.getEmail());
            if (user.isPresent()) {
                emailService.sendOTPEmail(request.getEmail(), otp);
            }

            LOGGER.info("OTP generated and sent via email for email: {}", request.getEmail());
            return new ServiceResponse(HttpStatus.OK.value(), "OTP sent via email", null);
        } catch (Exception e) {
            LOGGER.error("Error occurred while generating OTP: {}", e.getMessage());
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    public ServiceResponse generateAlexaCode(String email) {
        try {
            String otp = getOTP(5);

            if (alexaMap.containsKey(otp)) {
                int i = 0;
                while (i != 5) {
                    if (alexaMap.containsKey(otp)) {
                        i++;
                        continue;
                    } else {
                        if (alexaMap.containsValue(email))
                            alexaMap.values().remove(email);

                        alexaMap.put(otp, email);
                    }
                }

                if (i == 5) {
                    LOGGER.error("Failed to generate Alexa code for email: {}", email);
                    return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to generate code", null);
                }
            } else {
                if (alexaMap.containsValue(email)) {
                    alexaMap.values().equals(email);
                }
                alexaMap.put(otp, email);
            }

            alexaExpiry.put(otp, addMinutesToDate(2, new Date()));
            LOGGER.info("Alexa code generated for email: {}", email);
            return new ServiceResponse(HttpStatus.OK.value(), "Code Generated", otp);
        } catch (Exception e) {
            LOGGER.error("Error occurred while generating Alexa code: {}", e.getMessage());
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    public ServiceResponse validateAlexaCode(AlexaCodeRequest request) {
        try {
            if (alexaExpiry.get(request.getCode()).after(new Date(System.currentTimeMillis()))) {
                Optional<UserDevice> uDevice = userDeviceRepository.findByDevice(request.getDeviceId());
                if (uDevice.isPresent()) {
                    LOGGER.warn("Device already registered for device ID: {}", request.getDeviceId());
                    return new ServiceResponse(HttpStatus.BAD_REQUEST.value(), "Device already registered", null);
                } else {
                    String email = alexaMap.get(request.getCode());
                    removeAlexaCode(request.getCode());
                    Optional<User> user = userRepository.findByEmail(email);

                    if (user.isPresent()) {
                        UserDevice userDevice = new UserDevice();
                        userDevice.setDeviceId(request.getDeviceId());
                        userDevice.setUserId(user.get().getId());
                        userDevice.setEmail(user.get().getEmail());
                        userDeviceRepository.save(userDevice);

                        LOGGER.info("Code validated for email: {}, device ID: {}", email, request.getDeviceId());
                        return new ServiceResponse(HttpStatus.OK.value(), "Code validated", null);
                    } else {
                        LOGGER.error("Cannot find user for device ID: {}", request.getDeviceId());
                        return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Cannot find user against this device", null);
                    }
                }
            } else {
                removeAlexaCode(request.getCode());
                LOGGER.warn("Alexa code expired: {}", request.getCode());
                return new ServiceResponse(HttpStatus.BAD_REQUEST.value(), "Code Expired", null);
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred while validating Alexa code: {}", e.getMessage());
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    private String getOTP(int digCount) {
        StringBuilder sb = new StringBuilder(digCount);
        Random rnd = new Random();
        for (int i = 0; i < digCount; i++)
            sb.append((char) ('0' + rnd.nextInt(10)));
        return sb.toString();
    }

    private static Date addMinutesToDate(int minutes, Date beforeTime) {
        long curTimeInMs = beforeTime.getTime();
        Date afterAddingMins = new Date(curTimeInMs + (minutes * 60000));
        return afterAddingMins;
    }

    private void removeAlexaCode(String code) {
        alexaExpiry.remove(code);
        alexaMap.remove(code);
    }
}
