package com.example.prescripto.Adapters;

import com.example.prescripto.Model.DoctorEntity;
import com.example.prescripto.Service.EmbeddableDocument;

import java.util.Map;

public class DoctorEmbeddingAdapter implements EmbeddableDocument {

    private final DoctorEntity doctor;

    public DoctorEmbeddingAdapter(DoctorEntity doctor) {
        this.doctor = doctor;
    }

    @Override
    public String getSourceType() {
        return "doctor";
    }

    @Override
    public String getSourceId() {
        return doctor.getId().toString();
    }

    @Override
    public String buildEmbeddingText() {
        return """
                Dr. %s is a %s with degree %s.
                Experience: %s years.
                Fees: %d.
                About: %s.
                Location: %s %s
                """.formatted(
                doctor.getName(),
                doctor.getSpeciality(),
                doctor.getDegree(),
                doctor.getExperience(),
                doctor.getFees(),
                doctor.getAbout(),
                doctor.getAddress().getAddress1(),
                doctor.getAddress().getAddress2()
        );
    }

    @Override
    public Map<String, Object> buildMetadata() {
        return Map.of(
                "speciality", doctor.getSpeciality(),
                "fees", doctor.getFees()
        );
    }
}

