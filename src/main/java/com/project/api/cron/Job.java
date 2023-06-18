package com.project.api.cron;

import com.project.api.dto.request.NamazTimeRequest;
import com.project.api.helper.Helper;
import com.project.api.model.Reminder;
import com.project.api.model.User;
import com.project.api.repository.ReminderRepository;
import com.project.api.repository.UserRepository;
import com.project.api.service.NamazService;
import com.project.api.service.UserService;
import kong.unirest.json.JSONObject;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(Job.class);

    private final UserRepository userRepository;

    private final ReminderRepository reminderRepository;

    private final NamazService namazService;


    @Scheduled(fixedDelay = 10800000)
    public void run(){

        LOGGER.info("*****************************");
        LOGGER.info("START REMINDERS UPDATE JOB");
        LOGGER.info("TIME: " + new Date());
        LOGGER.info("*****************************");

        try{
            //Fetch All users
            List<User> users = userRepository.findAll().stream().filter(u -> u.getTimeZone() != null && u.getLng() != null && u.getLat() !=null).toList();

            //Fetch reminders of users
            for(User user : users){
                Optional<List<Reminder>> optionalReminders = reminderRepository.findByEmail(user.getEmail());
                if(optionalReminders.isPresent()){

                    List<Reminder> reminders = optionalReminders.get();

                    String currentDate = Helper.getCurrentDateByTimezone(user.getTimeZone());

                    if(Helper.compareDate(
                            currentDate,reminders.get(0).getUpdatedDate()
                    ) <= 0) continue;

                    NamazTimeRequest request = new NamazTimeRequest();
                    request.setTimeZone(currentDate);

                    JSONObject timings = namazService.getNamazTimings(request,user.getEmail());

                    if(timings == null) continue;

                    for(Reminder reminder : reminders){
                        reminder.setTime(timings.getString(reminder.getNamaz()).split(" ")[0]);
                        reminder.setUpdatedDate(currentDate);
                        reminderRepository.save(reminder);
                    }
                }
            }
        }
        catch (Exception ex){
            LOGGER.error("Exception occurred in Reminders update Job {}",ex.getMessage());
            ex.printStackTrace();
        }
        LOGGER.info("REMINDERS JOB COMPLETED");
    }

}
