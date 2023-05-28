package com.project.api.service;

import com.project.api.dto.response.ReminderDTO;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.model.Reminder;
import com.project.api.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.boot.Banner;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository repository;

    private final ModelMapper modelMapper;


    public ServiceResponse save(Reminder reminder, Principal principal){

        try{
            Optional<Reminder> reminderDb =  repository.findByEmailAndNamaz(principal.getName(), reminder.getNamaz());

            if(!reminderDb.isPresent()) {
                reminder.setEmail(principal.getName());
                repository.save(reminder);
            }
            else{
                Reminder updatedReminder =  reminderDb.get();
                updatedReminder.setAudioFile(reminder.getAudioFile());
                updatedReminder.setTime(reminder.getTime());
                repository.update(updatedReminder);
            }

            return new ServiceResponse(HttpStatus.OK.value(),"SUCCESS",null);
        }catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage(),null);
        }
    }


    public ServiceResponse getReminders(Principal principal){

        try{
            Optional<List<Reminder>> reminderDb =  repository.findByEmail(principal.getName());

            if(reminderDb.isPresent()) {
                modelMapper.map(reminderDb.get(), new TypeToken<List<ReminderDTO>>(){}.getType());
               return new ServiceResponse(HttpStatus.OK.value(), "SUCCESS",
                       modelMapper.map(reminderDb.get(), new TypeToken<List<ReminderDTO>>(){}.getType()));
            }
            else
                return new ServiceResponse(HttpStatus.NOT_FOUND.value(), "No Reminders Exist!",null);
        }catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage(),null);
        }
    }

}
