package com.example.prescripto.Service.Implementation;

import com.example.prescripto.Model.AppointmentEntity;
import com.example.prescripto.Service.PaymentService;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${stripe.currency}")
    private String currency;

    public String createStripeCheckoutSession(Long appointmentId, String origin,Long amount) throws Exception {



        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(currency.toLowerCase())
                        .setUnitAmount(amount * 100)
                        .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Appointment Fees")
                                        .build()
                        )
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setPriceData(priceData)
                        .setQuantity(1L)
                        .build();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(origin + "/verify?success=true&appointmentId=" + appointmentId)
                        .setCancelUrl(origin + "/verify?success=false&appointmentId=" + appointmentId)
                        .addLineItem(lineItem)
                        .build();

        Session session = Session.create(params);
        return session.getUrl();
    }
}
