package com.project.api.service;

import com.google.gson.JsonObject;
import com.project.api.dto.request.NamazTimeRequest;
import com.project.api.dto.response.ReminderDTO;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.helper.Constants;
import com.project.api.helper.Helper;
import com.project.api.model.Reminder;
import com.project.api.repository.ReminderRepository;
import com.project.api.repository.UserRepository;
import kong.unirest.json.JSONObject;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
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

    @Value("${aws.s3.url}")
    private String s3Url;


    public ServiceResponse save(ReminderDTO reminder, Principal principal){

        try{
            Optional<Reminder> reminderDb =  repository.findByEmailAndNamaz(principal.getName(), reminder.getNamaz());

            if(reminder.getIsEnabled()){
                assert reminder.getTime() != null : "Reminder time cannot be empty";
                assert reminder.getAudioFile()!=null : "Please select audio before enabling reminder";
            }

            Reminder reminderObj = this.modelMapper.map(reminder,Reminder.class);

            reminderObj.setEmail(principal.getName());
            reminderObj.setAdjustedTime(reminderObj.getAdjustedTime() == null ? 0 : reminderObj.getAdjustedTime());
            reminderObj.setAudioUrl(s3Url + reminder.getAudioFile());

            if(reminderDb.isPresent()) {
                reminderObj.setId(reminderDb.get().getId());
                repository.update(reminderObj);
            }
            else{

                repository.save(reminderObj);
            }

            return new ServiceResponse(HttpStatus.OK.value(),"SUCCESS",null);
        }catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage(),null);
        }
    }


    public ServiceResponse getReminders(String email){

        try{
            Optional<List<Reminder>> reminderDb =  repository.findByEmail(email);

            if(reminderDb.isPresent()) {
               return new ServiceResponse(HttpStatus.OK.value(), "SUCCESS",
                       modelMapper.map(reminderDb.get(), new TypeToken<List<ReminderDTO>>(){}.getType()));
            }
            else
                return new ServiceResponse(HttpStatus.NOT_FOUND.value(), "No Reminders Exist!",null);
        }catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage(),null);
        }
    }

    public ServiceResponse getReminders(NamazTimeRequest request, Principal principal) {
        try {
            // Retrieve reminders from the database
            Optional<List<Reminder>> reminderDb = repository.findByEmail(principal.getName());

            // Retrieve namaz timings
            ServiceResponse response = namazService.getNamazTimings(request, principal);

            if (response.getCode() != 200) {
                return response;
            }

            JSONObject object = new JSONObject((String)(response.getData()));

            // Map Reminder entities to ReminderDTOs if present, otherwise use an empty list
            List<ReminderDTO> reminders = (List<ReminderDTO>) reminderDb.map(r -> modelMapper.map(r, new TypeToken<List<ReminderDTO>>(){}.getType()))
                    .orElseGet(ArrayList::new);

            // Create empty reminders
            List<ReminderDTO> emptyReminders = createEmptyReminders(object, reminders);

            return new ServiceResponse(HttpStatus.OK.value(), "Success", emptyReminders);
        } catch (Exception e) {
            // Return error response if an exception occurs
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    private List<ReminderDTO> createEmptyReminders(JSONObject object, List<ReminderDTO> exclude) {
        List<ReminderDTO> list = new ArrayList<>();

        List<String> namazReminders = exclude.stream().map(ReminderDTO::getNamaz).toList();

        // Create empty reminders for each namaz
        for (String namaz : Constants.namaz) {
            if (!namazReminders.contains(namaz)) {
                ReminderDTO dto = new ReminderDTO();
                dto.setNamaz(namaz);
                dto.setAdjustedTime(0);
                dto.setIsEnabled(false);
                dto.setTime(object.getString(namaz));
                list.add(dto);
            }
            else
                list.add(exclude.stream().filter(x -> x.getNamaz().equalsIgnoreCase(namaz)).findFirst().get());

        }

        return list;
    }
}
