package com.project.api.service;

import com.project.api.dto.request.NamazTimeRequest;
import com.project.api.dto.response.ServiceResponse;
import com.project.api.helper.Helper;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class NamazService {

    @Value("${namaz.time.url}")
    private String NAMAZ_TIME_URL;

    @Value("${namaz.method.url}")
    private String NAMAZ_METHOD_URL ;

    public ServiceResponse getNamazMethods() {
        try {
            HttpResponse<JsonNode> response = Unirest.get(NAMAZ_METHOD_URL).asJson();

            if (response.isSuccess()) {
                JSONObject responseData = response.getBody().getObject().getJSONObject("data");
                return new ServiceResponse(HttpStatus.OK.value(), "SUCCESS", responseData.toString());
            } else {
                return new ServiceResponse(response.getStatus(), "Unable to fetch calculation methods", null);
            }
        } catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }

    public ServiceResponse getNamazTimings(@RequestBody NamazTimeRequest request) {

        try {
            String formattedUrl = Helper.formatTimingsURL(NAMAZ_TIME_URL, request);
            HttpResponse<JsonNode> response = Unirest.get(formattedUrl).asJson();

            if (response.isSuccess()) {
                JSONArray data = response.getBody().getObject().getJSONArray("data");
                JSONObject timings = data.getJSONObject(Helper.getDayFromDate(request.getTimeZone()) - 1).getJSONObject("timings");
                return new ServiceResponse(HttpStatus.OK.value(), "SUCCESS", timings.toString());
            } else {
                return new ServiceResponse(response.getStatus(), "Unable to fetch namaz timings", null);
            }
        } catch (Exception e) {
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null);
        }
    }
}
