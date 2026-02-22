package com.example.prescripto.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;

public class IdDTO {

    @JsonAlias({"appointmentId", "id"})
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
