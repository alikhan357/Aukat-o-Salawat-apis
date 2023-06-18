package com.project.api.service;

import com.google.gson.JsonObject;
import com.project.api.dto.request.NamazTimeRequest;
import com.project.api.dto.response.ReminderDTO;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.helper.Constants;
import com.project.api.helper.Helper;
import com.project.api.model.Reminder;
import com.project.api.model.User;
import com.project.api.repository.ReminderRepository;
import com.project.api.repository.UserRepository;
import kong.unirest.json.JSONObject;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository repository;
    private final ModelMapper modelMapper;
    private final NamazService namazService;

    private final UserRepository userRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReminderService.class);

    @Value("${aws.s3.url}")
    private String s3Url;

    public ServiceResponse save(ReminderDTO reminder, Principal principal) {
        try {

            //fetch user
            Optional<User> user = userRepository.findByEmail(principal.getName());

            Optional<Reminder> reminderDb = repository.findByEmailAndNamaz(principal.getName(), reminder.getNamaz());

            if (reminder.getIsEnabled()) {
                assert reminder.getTime() != null : "Reminder time cannot be empty";
                assert reminder.getAudioFile() != null : "Please select audio before enabling reminder";
                assert user.get().getTimeZone() != null : "Please update you location";
            }

            Reminder reminderObj = modelMapper.map(reminder, Reminder.class);

            reminderObj.setEmail(principal.getName());
            reminderObj.setAdjustedTime(reminderObj.getAdjustedTime() == null ? 0 : reminderObj.getAdjustedTime());
            reminderObj.setAudioUrl(s3Url + reminder.getAudioFile());
            reminderObj.setTimeZone(user.get().getTimeZone());
            reminderObj.setCreatedDate(Helper.getCurrentDateByTimezone(user.get().getTimeZone()));
            reminderObj.setUpdatedDate(reminderObj.getCreatedDate());

            if (reminderDb.isPresent()) {
                reminderObj.setId(reminderDb.get().getId());
                repository.update(reminderObj);
            } else {
                repository.save(reminderObj);
            }

            return new ServiceResponse(HttpStatus.OK.value(), "SUCCESS", null);
        } catch (Exception e) {
            LOGGER.error("Error occurred while saving reminder: {}", e.getMessage());
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    public ServiceResponse getReminders(String serial){
        try {
            //fetch user
            Optional<User> user = userRepository.findBySerial(serial);
            if(user.isPresent()) {
                String email = user.get().getEmail();
                Optional<List<Reminder>> reminderDb = repository.findByEmail(user.get().getEmail());

                if (reminderDb.isPresent()) {
                    List<ReminderDTO> reminderDTOs = modelMapper.map(reminderDb.get(), new TypeToken<List<ReminderDTO>>() {
                    }.getType());
                    LOGGER.info("Retrieved reminders for email: {}", email);
                    return new ServiceResponse(HttpStatus.OK.value(), "SUCCESS", reminderDTOs);
                } else {
                    LOGGER.warn("No reminders exist for email: {}", email);
                    return new ServiceResponse(HttpStatus.NOT_FOUND.value(), "No reminders exist", null);
                }
            }
            LOGGER.warn("No User exist for serial: {}", serial);
            return new ServiceResponse(HttpStatus.NOT_FOUND.value(), "User does not exist against serial", null);
        } catch (Exception e) {
            LOGGER.error("Error occurred while retrieving reminders: {}", e.getMessage());
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    public ServiceResponse getReminders(NamazTimeRequest request, Principal principal) {
        try {
            // Retrieve reminders from the database
            Optional<List<Reminder>> reminderDb = repository.findByEmail(principal.getName());

            // Retrieve namaz timings
            ServiceResponse response = namazService.getNamazTimings(request, principal);

            if (response.getCode() != HttpStatus.OK.value()) {
                LOGGER.warn("Error occurred while retrieving namaz timings: {}", response.getMessage());
                return response;
            }

            JSONObject namazTimings = new JSONObject(response.getData().toString());

            // Map Reminder entities to ReminderDTOs if present, otherwise use an empty list
            List<ReminderDTO> reminders = (List<ReminderDTO>) reminderDb.map(r -> modelMapper.map(r, new TypeToken<List<ReminderDTO>>(){}.getType()))
                    .orElseGet(ArrayList::new);
            // Create empty reminders for missing namaz
            List<ReminderDTO> emptyReminders = createEmptyReminders(namazTimings, reminders);

            LOGGER.info("Retrieved reminders for user: {}", principal.getName());
            return new ServiceResponse(HttpStatus.OK.value(), "SUCCESS", emptyReminders);
        } catch (Exception e) {
            LOGGER.error("Error occurred while retrieving reminders: {}", e.getMessage());
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    private List<ReminderDTO> createEmptyReminders(JSONObject namazTimings, List<ReminderDTO> reminders) {
        List<ReminderDTO> emptyReminders = new ArrayList<>();

        List<String> namazReminders = reminders.stream().map(ReminderDTO::getNamaz).collect(Collectors.toList());

        // Create empty reminders for each namaz
        for (String namaz : Constants.namaz) {
            if (!namazReminders.contains(namaz)) {
                ReminderDTO emptyReminder = new ReminderDTO();
                emptyReminder.setNamaz(namaz);
                emptyReminder.setAdjustedTime(0);
                emptyReminder.setIsEnabled(false);
                emptyReminder.setTime(namazTimings.getString(namaz));
                emptyReminders.add(emptyReminder);
            } else {
                ReminderDTO reminder = reminders.stream().filter(r -> r.getNamaz().equalsIgnoreCase(namaz)).findFirst().get();
                emptyReminders.add(reminder);
            }
        }

        return emptyReminders;
    }
}
