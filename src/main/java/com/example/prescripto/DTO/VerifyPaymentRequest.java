package com.example.prescripto.DTO;

import lombok.Data;

@Data
public class VerifyPaymentRequest {

     private String success;

     private String appointmentId;


    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }
}
