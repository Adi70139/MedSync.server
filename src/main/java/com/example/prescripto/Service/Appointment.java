package com.example.prescripto.Service;

import com.example.prescripto.Utils.APIResponse;
import com.example.prescripto.Utils.StripeResponse;
import org.springframework.http.ResponseEntity;

public interface Appointment {

        APIResponse getAllAvailableSlots();

        APIResponse bookAppointment(Long userId,String email, Long doctorId, String slotDate, String slotTime, Long amount);

        APIResponse getUserAppointments(Long userId);

        StripeResponse makePayment(String appointmentID, String origin) throws Exception;

        APIResponse verifyPayemnt(String appointmentId, String success);

        APIResponse calcelAppointment(String appointmentId);

       APIResponse getAppointmentsForDoctor(Long id);

        APIResponse completeAppointment(String id);
}
