package com.example.prescripto.DTO;

public class GetAppointmentResponse {

     private AppointmentResponseDTO appointment;
     private DoctorResponseDTO doctor;

     private UserDTO user;

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public GetAppointmentResponse(DoctorResponseDTO doctorResponseDTO, AppointmentResponseDTO appointmentResponseDTO) {
        this.doctor = doctorResponseDTO;
        this.appointment = appointmentResponseDTO;
    }

    public GetAppointmentResponse(AppointmentResponseDTO appointment, DoctorResponseDTO doctor, UserDTO user) {
        this.appointment = appointment;
        this.doctor = doctor;
        this.user = user;
    }

    public AppointmentResponseDTO getAppointment() {
        return appointment;
    }

    public void setAppointment(AppointmentResponseDTO appointment) {
        this.appointment = appointment;
    }

    public DoctorResponseDTO getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorResponseDTO doctor) {
        this.doctor = doctor;
    }
}
