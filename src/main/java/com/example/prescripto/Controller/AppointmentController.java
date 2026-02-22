package com.example.prescripto.Controller;


import com.example.prescripto.DTO.StripePaymentRequest;
import com.example.prescripto.DTO.VerifyPaymentRequest;
import com.example.prescripto.Service.Appointment;
import com.example.prescripto.Utils.APIResponse;
import com.example.prescripto.Utils.StripeResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class AppointmentController {

      @Autowired
      private Appointment appointmentService;

      @GetMapping("/api/get-appointments")
      public APIResponse getAvailableSlots(@RequestHeader("Authorization") String authHeader){
          return appointmentService.getAllAvailableSlots();

      }

      @PostMapping("/api/appointment/payment-stripe")
      public ResponseEntity<StripeResponse> makePayment(@RequestHeader("Authorization") String authHeader,
                                                       @RequestBody StripePaymentRequest paymentRequest,
                                                       @RequestHeader("Origin") String origin) throws Exception {


           return ResponseEntity.ok(appointmentService.makePayment(paymentRequest.getAppointmentId(),origin));
      }

      @PostMapping("/api/appointment/verify-stripe")
      public APIResponse verifyPayemnt(@RequestHeader("Authorization") String authHeader,
                                       @RequestBody VerifyPaymentRequest paymentRequest){

           appointmentService.verifyPayemnt(paymentRequest.getAppointmentId(),paymentRequest.getSuccess());

            return null;
      }


      @PostMapping("/api/appointment/cancel-appointment")
      public APIResponse cancelAppointment(@RequestHeader("Authorization") String authHeader,
                                           @RequestBody StripePaymentRequest appointmentId){

              return appointmentService.calcelAppointment(appointmentId.getAppointmentId());
      }
}
