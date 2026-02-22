package com.example.prescripto.DTO;


import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
public class AllAppointmentsResDTO {


    private Object userData;


    private Object doctorData;

    private boolean cancelled;

    private boolean completed;

    private String slotDate;

    private String slotTime;

    private Long amount;

    private Long id;

    // convenience fields requested: name and image at appointment level
    private String name;
    private String image;

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getSlotDate() {
        return slotDate;
    }

    public void setSlotDate(String slotDate) {
        this.slotDate = slotDate;
    }

    public String getSlotTime() {
        return slotTime;
    }

    public void setSlotTime(String slotTime) {
        this.slotTime = slotTime;
    }

    public boolean getCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Object getUserData() {
        return userData;
    }

    public void setUserData(Object userData) {
        this.userData = userData;
    }

    public Object getDoctorData() {
        return doctorData;
    }

    public void setDoctorData(Object doctorData) {
        this.doctorData = doctorData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
