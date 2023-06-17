package com.project.api.Controller;

import com.project.api.dto.request.NamazTimeRequest;
import com.project.api.dto.response.ReminderDTO;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.model.Reminder;
import com.project.api.service.NamazService;
import com.project.api.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/reminder")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReminderController.class);

    @PostMapping("/save")
    public ResponseEntity<ServiceResponse> save(@RequestBody ReminderDTO reminder, Principal principal){
        LOGGER.info("save called {} by {}",reminder,principal.getName());
        ServiceResponse response = reminderService.save(reminder,principal);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/")
    public ResponseEntity<ServiceResponse> get(Principal principal){
        LOGGER.info("get by {}",principal.getName());
        ServiceResponse response = reminderService.getReminders(principal.getName());
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/")
    public ResponseEntity<ServiceResponse> getReminders(@RequestBody NamazTimeRequest request, Principal principal){
        LOGGER.info("getReminders called {} by {}",request,principal.getName());
        ServiceResponse response = reminderService.getReminders(request,principal);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/pi/reminders/{serial}")
    public ResponseEntity<ServiceResponse> getPiReminders(@PathVariable String serial){
        LOGGER.info("getPiReminders called SerialNumber: {} ",serial);
        ServiceResponse response = reminderService.getReminders(serial);
        return ResponseEntity.status(response.getCode()).body(response);
    }


}

