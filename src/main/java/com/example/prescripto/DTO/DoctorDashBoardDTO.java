package com.example.prescripto.DTO;

import java.io.Serializable;
import java.util.List;

public class DoctorDashBoardDTO implements Serializable {


     private Long earnings;

     private Long patients;

     private Long appointments;


     private List<DoctorAppointmentsDTO> latestAppointments;

    public List<DoctorAppointmentsDTO> getLatestAppointments() {
        return latestAppointments;
    }

    public void setLatestAppointments(List<DoctorAppointmentsDTO> latestAppointments) {
        this.latestAppointments = latestAppointments;
    }

    public Long getEarnings() {
        return earnings;
    }

    public void setEarnings(Long earnings) {
        this.earnings = earnings;
    }

    public Long getPatients() {
        return patients;
    }

    public void setPatients(Long patients) {
        this.patients = patients;
    }

    public Long getAppointments() {
        return appointments;
    }

    public void setAppointments(Long appointments) {
        this.appointments = appointments;
    }


}
