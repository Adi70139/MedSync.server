package com.example.prescripto.Service;

public interface PaymentService {

    String createStripeCheckoutSession(Long appointmentId, String origin, Long amount) throws Exception;
}
