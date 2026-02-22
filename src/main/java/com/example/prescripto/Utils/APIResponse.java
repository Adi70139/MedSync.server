package com.example.prescripto.Utils;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class APIResponse implements Serializable {

    private String message;

    private boolean success;

    private Object data;

    private String token;


    public APIResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public APIResponse(boolean success,String message,  Object data) {
        this.message = message;
        this.success = success;
        this.data = data;
    }

    public APIResponse(boolean success, Object data) {
        this.success = success;
        this.data = data;
    }



    public APIResponse(boolean success, String message, String token) {
        this.success = success;
        this.message = message;
        this.token = token;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}