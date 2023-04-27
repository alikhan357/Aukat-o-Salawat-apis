package com.project.api.service;

import com.project.api.dto.request.OTPGenRequest;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.model.User;
import com.project.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OTPService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailService emailService;
    private static Map<String,String> otpMap = new HashMap<>();
    private static Map<String, Date> otpExpiry = new HashMap<>();

    public ServiceResponse generateOTP(OTPGenRequest request){

        //generate OTP
        String otp = getOTP(5);
        //save in map
        otpMap.put(request.getEmail(),otp);
        //check if email belongs to a user
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if(user.isPresent()){
            //send otp via email
            emailService.sendOTPEmail(request.getEmail(),otp);
        }

        return new ServiceResponse(HttpStatus.OK,"OTP Send via email",null);


    }



    private String getOTP(int digCount) {
        StringBuilder sb = new StringBuilder(digCount);
        Random rnd = new Random();
        for(int i=0; i < digCount; i++)
            sb.append((char)('0' + rnd.nextInt(10)));
        return sb.toString();
    }




}
