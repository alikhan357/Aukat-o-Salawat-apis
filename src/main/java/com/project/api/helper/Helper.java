package com.project.api.helper;


import com.project.api.dto.request.NamazTimeRequest;
import org.joda.time.format.DateTimeFormat;
import java.time.format.DateTimeFormatter;

import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Helper {

    public static int getDayFromDate(String date){
        return DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(date).toLocalDateTime().getDayOfMonth();
    }

    public static int getYearFromDate(String date){
        return DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(date).toLocalDateTime().getYear();
    }

    public static int getMonthFromDate(String date){
        return DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(date).toLocalDateTime().getMonthOfYear();
    }

    public static String formatTimingsURL(String url , NamazTimeRequest request){
        url = url + "/" + getYearFromDate(request.getTimeZone());
        url = url + "/" + getMonthFromDate(request.getTimeZone());

        UriComponentsBuilder URL = UriComponentsBuilder.fromUriString(url)
                .queryParam("latitude", request.getLat())
                .queryParam("longitude",request.getLng())
                .queryParam("school", request.getSchool() == null ? 0 : request.getSchool());

        if(request.getMethod() != null)
            URL.queryParam("method",request.getMethod());

        return URL.build().toUri().toString();

    }

    public static String getCurrentDateByTimezone(String timezone){

        // Get the current date and time in the specified timezone
        ZonedDateTime dateTime = ZonedDateTime.now(ZoneId.of(timezone));

        // Extract the date from the ZonedDateTime
        LocalDate currentDate = dateTime.toLocalDate();

        // Format the date as per your requirements
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return currentDate.format(formatter);
    }

    /**
     * IF date1 > date2 return +ve
     * IF date1 < date2 return -ve
     * else return 0
     */
    public static Integer compareDate(String date1String,String date2String){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate date1 = LocalDate.parse(date1String, formatter);
        LocalDate date2 = LocalDate.parse(date2String, formatter);

        return date1.compareTo(date2);
    }

}
