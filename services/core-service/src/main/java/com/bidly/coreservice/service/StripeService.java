package com.bidly.coreservice.service;

import com.bidly.coreservice.dto.stripe.CreatePaymentIntentDTO;
import com.bidly.coreservice.dto.stripe.PaymentIntentResponseDTO;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.publishable.key}")
    private String publishableKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public PaymentIntentResponseDTO createPaymentIntent(CreatePaymentIntentDTO dto, String userId) throws StripeException {
        // Stripe expects amounts in cents
        long amountInCents = dto.getAmount().multiply(new java.math.BigDecimal(100)).longValue();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("usd")
                .putMetadata("userId", userId)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        PaymentIntent intent = PaymentIntent.create(params);

        return new PaymentIntentResponseDTO(intent.getClientSecret(), publishableKey);
    }

    public PaymentIntent retrievePaymentIntent(String id) throws StripeException {
        return PaymentIntent.retrieve(id);
    }

    public Charge retrieveCharge(String id) throws StripeException {
        return Charge.retrieve(id);
    }
}
