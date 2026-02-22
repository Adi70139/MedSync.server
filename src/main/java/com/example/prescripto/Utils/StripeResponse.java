package com.example.prescripto.Utils;


public class StripeResponse {

    private boolean success;

    private String sessionUrl;

    public StripeResponse(boolean success, String sessionUrl) {
        this.success = success;
        this.sessionUrl = sessionUrl;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getSessionUrl() {
        return sessionUrl;
    }

    public void setSessionUrl(String sessionUrl) {
        this.sessionUrl = sessionUrl;
    }
}
