package com.project.api.service;

import com.project.api.dto.request.AlexaCodeRequest;
import com.project.api.dto.request.OTPGenRequest;
import com.project.api.dto.request.OTPValidateRequest;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.model.User;
import com.project.api.model.UserDevice;
import com.project.api.repository.UserDeviceRepository;
import com.project.api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
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

    public ServiceResponse generateOTP(OTPGenRequest request) {
        try {
            Optional<User> user = userRepository.findByEmail(request.getEmail());

            if (user.isPresent()) {

                String otp = getOTP(5);
                otpMap.put(request.getEmail(), otp);
                otpExpiry.put(otp, addMinutesToDate(2, new Date()));
                emailService.sendOTPEmail(request.getEmail(), otp);
            }

            LOGGER.info("OTP generated and sent via email for email: {}", request.getEmail());
            return new ServiceResponse(HttpStatus.OK.value(), "OTP sent via email", null);
        } catch (Exception e) {
            LOGGER.error("Error occurred while generating OTP: {}", e.getMessage());
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }
    public ServiceResponse validateOTP(OTPValidateRequest request){

        //OTP correct and not expired
        try {
            if (otpMap.get(request.getEmail()).equals(request.getOtp())
                    && otpExpiry.get(request.getOtp()).after(new Date(System.currentTimeMillis()))) {
                return new ServiceResponse(HttpStatus.OK.value(), "OTP VALIDATED", null);
            } else
                return new ServiceResponse(HttpStatus.BAD_REQUEST.value(), "INVALID OTP OR EXPIRED", null);
        }

        catch (Exception ex){
            ex.printStackTrace();
            LOGGER.error("Error occurred at validate OTP {}",ex.getMessage());
            return new ServiceResponse(HttpStatus.BAD_REQUEST.value(), "Unable To Process Request At This Time", null);
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

    @Scheduled(fixedDelay = 3600000 )
    public void evictExpiredTokens(){
        for(Map.Entry<String, String> entry : otpMap.entrySet()) {

            String otp = entry.getValue();
            String email = entry.getKey();

            if(otpExpiry.get(otp).before(new Date())){
                otpMap.remove(email);
                otpExpiry.remove(otp);
            }
        }
    }
}
