package com.project.api.service;

import com.project.api.dto.request.AlexaCodeRequest;
import com.project.api.dto.request.OTPGenRequest;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.model.User;
import com.project.api.model.UserDevice;
import com.project.api.repository.UserDeviceRepository;
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

    @Autowired
    UserDeviceRepository userDeviceRepository;


    private static Map<String,String> otpMap = new HashMap<>();
    private static Map<String, Date> otpExpiry = new HashMap<>();
    private static Map<String,String> alexaMap = new HashMap<>();
    private static Map<String, Date> alexaExpiry = new HashMap<>();

    public ServiceResponse generateOTP(OTPGenRequest request){

        try {
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
            return new ServiceResponse(HttpStatus.OK.value(),"OTP Send via email",null);
        } catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage(),null);
        }

    }

    public ServiceResponse generateAlexaCode(OTPGenRequest request){
        try {
            //generate OTP
            String otp = getOTP(5);
            if(alexaMap.containsKey(otp)){
                int i = 0;
                while(i != 5){
                    if(alexaMap.containsKey(otp)){
                        i++;
                        continue;
                    }
                    else {
                        if(alexaMap.containsValue(request.getEmail()))
                            alexaMap.values().remove(request.getEmail());

                        alexaMap.put(otp, request.getEmail());
                    }
                }

                if(i==5)
                    return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Failed to generate code",null);
            }
            else {
                if(alexaMap.containsValue(request.getEmail())) {
                    alexaMap.values().equals(request.getEmail());
                }
                alexaMap.put(otp, request.getEmail());
            }

            alexaExpiry.put(otp,addMinutesToDate(2,new Date()));
            return new ServiceResponse(HttpStatus.OK.value(),"Code Generated",otp);
        } catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage(),null);
        }

    }

    public ServiceResponse validateAlexaCode(AlexaCodeRequest request){

        try {
            //not expired
            if(alexaExpiry.get(request.getCode()).after(new Date(System.currentTimeMillis()))){
                Optional<UserDevice> uDevice = userDeviceRepository.findByDevice(request.getDeviceId());
                if(uDevice.isPresent()){
                    return new ServiceResponse(HttpStatus.BAD_REQUEST.value(),"Device already registered",null);
                }
                else{

                    String email = alexaMap.get(request.getCode());
                    removeAlexaCode(request.getCode());
                    Optional<User> user = userRepository.findByEmail(email);

                    if(user.isPresent()){
                        UserDevice userDevice = new UserDevice();

                        userDevice.setDeviceId(request.getDeviceId());
                        userDevice.setUserId(user.get().getId());
                        userDevice.setEmail(user.get().getEmail());

                        userDeviceRepository.save(userDevice);

                        return new ServiceResponse(HttpStatus.OK.value(),"Code validated",null);

                    }
                    else
                        return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Cannot Find user against this device",null);
                }
            }else {
                removeAlexaCode(request.getCode());
                return new ServiceResponse(HttpStatus.BAD_REQUEST.value(), "Code Expired", null);
            }
        } catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage(),null);
        }

    }

    private String getOTP(int digCount) {
        StringBuilder sb = new StringBuilder(digCount);
        Random rnd = new Random();
        for(int i=0; i < digCount; i++)
            sb.append((char)('0' + rnd.nextInt(10)));
        return sb.toString();
    }

    private static Date addMinutesToDate(int minutes, Date beforeTime) {

        long curTimeInMs = beforeTime.getTime();
        Date afterAddingMins = new Date(curTimeInMs
                + (minutes * 60000));
        return afterAddingMins;
    }

    private void removeAlexaCode(String code){
        alexaExpiry.remove(code);
        alexaMap.remove(code);
    }




}