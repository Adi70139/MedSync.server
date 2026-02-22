package com.example.prescripto.Adapters;

import com.example.prescripto.Model.AppointmentEntity;
import com.example.prescripto.Model.DoctorEntity;
import com.example.prescripto.Service.EmbeddableDocument;

import java.util.HashMap;
import java.util.Map;

public class AppointmentEmbeddingAdapter implements EmbeddableDocument {

    private final AppointmentEntity appointment;
    private final DoctorEntity doctor;
    private final String userEmail;

    public AppointmentEmbeddingAdapter(AppointmentEntity appointment, DoctorEntity doctor, String userEmail) {
        this.appointment = appointment;
        this.doctor = doctor;
        this.userEmail = userEmail;
    }

    @Override
    public String getSourceType() {
        return "appointment";
    }

    @Override
    public String getSourceId() {
        return appointment.getId() == null ? String.valueOf(appointment.hashCode()) : appointment.getId().toString();
    }

    @Override
    public String buildEmbeddingText() {
        return String.format(
                "Appointment for user %s with Dr. %s (%s).\nDate: %s\nTime: %s\nAmount: %s\nPayment: %s\n",
                userEmail != null ? userEmail : appointment.getUserId(),
                doctor != null ? doctor.getName() : "Unknown",
                doctor != null ? doctor.getSpeciality() : "Unknown",
                appointment.getSlotDate(),
                appointment.getSlotTime(),
                appointment.getAmount(),
                appointment.getPayment()
        );
    }

    @Override
    public Map<String, Object> buildMetadata() {
        Map<String, Object> m = new HashMap<>();
        if (doctor != null) {
            m.put("doctorId", doctor.getId());
            m.put("doctorName", doctor.getName());
            m.put("doctorSpeciality", doctor.getSpeciality());
        }
        m.put("slotDate", appointment.getSlotDate());
        m.put("slotTime", appointment.getSlotTime());
        m.put("amount", appointment.getAmount());
        m.put("payment", appointment.getPayment());
        m.put("userId", appointment.getUserId());
        m.put("cancelled", appointment.getCancelled());
        m.put("completed", appointment.getCompleted());
        return m;
    }
}

