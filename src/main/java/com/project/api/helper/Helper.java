package com.project.api.helper;


import com.project.api.dto.request.NamazTimeRequest;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

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

}
