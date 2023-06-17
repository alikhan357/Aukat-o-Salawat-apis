package com.project.api.service;


import com.project.api.dto.response.ServiceResponse;
import com.project.api.model.UserDevice;
import com.project.api.repository.UserDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final UserDeviceRepository userDeviceRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);

    /**
     * Validates the device based on the provided deviceId.
     *
     * @param deviceId The device ID to validate.
     * @return ServiceResponse object indicating the result of the device validation.
     */
    public ServiceResponse validateDevice(String deviceId) {
        try {
            Optional<UserDevice> user = userDeviceRepository.findByDevice(deviceId);

            if (user.isPresent()) {
                LOGGER.info("Device validation successful for device ID: {}", deviceId);
                return new ServiceResponse(HttpStatus.OK.value(), "Success", null);
            } else {
                LOGGER.warn("Device not found for device ID: {}", deviceId);
                return new ServiceResponse(HttpStatus.NOT_FOUND.value(), "Data Not Found", null);
            }
        } catch (Exception e) {
            LOGGER.error("Error occurred while validating device: {}", e.getMessage());
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }
}
