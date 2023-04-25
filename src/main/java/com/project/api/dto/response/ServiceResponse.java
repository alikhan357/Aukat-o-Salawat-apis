package com.project.api.dto.response;

import org.springframework.http.HttpStatus;

public class ServiceResponse {

    public ServiceResponse() {

    }

    private HttpStatus code;
    private String message;
    private Object data;

    // Function is used to set the exception need to refactor it.
    public ServiceResponse(HttpStatus code, String message, Object object) {
        super();
        this.code = code;
        this.message = message;
        this.data = object;
    }

    public HttpStatus getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public synchronized void setCode(HttpStatus code) {
        this.code = code;
    }

    public synchronized void setMessage(String message) {
        this.message = message;
    }

    public synchronized void setData(Object data) {
        this.data = data;
    }


}
