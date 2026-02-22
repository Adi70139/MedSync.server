package com.example.prescripto.DTO;

public class DoctorAppointmentsDTO {


     private String name;

     private String image;

     private String slotDate;

     private boolean cancelled;

     private boolean completed;

     private boolean payment;

    public boolean getPayment() {
        return payment;
    }


    public void setPayment(boolean payment) {
        this.payment = payment;
    }



    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getSlotDate() {
        return slotDate;
    }

    public void setSlotDate(String slotDate) {
        this.slotDate = slotDate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
