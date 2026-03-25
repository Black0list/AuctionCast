package com.bidly.coreservice.controller;

import com.bidly.common.dto.ApiResponse;
import com.bidly.coreservice.dto.stripe.CreatePaymentIntentDTO;
import com.bidly.coreservice.dto.stripe.PaymentIntentResponseDTO;
import com.bidly.coreservice.service.StripeService;
import com.bidly.coreservice.service.WalletService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
public class StripeController {

    private final StripeService stripeService;
    private final WalletService walletService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping("/create-payment-intent")
    public ApiResponse<PaymentIntentResponseDTO> createPaymentIntent(
            @Valid @RequestBody CreatePaymentIntentDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        log.info("--- Creating PaymentIntent Request ---");
        log.info("User ID from JWT: {}", userId);
        log.info("Requested Amount: {}", dto.getAmount());
        
        try {
            PaymentIntentResponseDTO response = stripeService.createPaymentIntent(dto, userId);
            log.info("PaymentIntent created successfully. ClientSecret: {}..., PublishableKey: {}...", 
                response.getClientSecret().substring(0, 10), 
                response.getPublishableKey().substring(0, 10));
            return ApiResponse.success(response, "Payment intent created");
        } catch (StripeException e) {
            log.error("Stripe API error: {}", e.getMessage(), e);
            return ApiResponse.error("Stripe error: " + e.getMessage());
        } catch (Exception e) {
            log.error("General error creating payment intent: {}", e.getMessage(), e);
            return ApiResponse.error("Internal error: " + e.getMessage());
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeEvent(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        log.info("--- Received Stripe Webhook ---");
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            log.info("Event Verified! Type: {}", event.getType());
        } catch (SignatureVerificationException e) {
            log.error("SIGNATURE FAILED: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Signature verification failed");
        }

        String eventType = event.getType();
        String userId = null;
        BigDecimal amountInDollars = BigDecimal.ZERO;
        Map<String, String> metadata = null;

        log.info("Processing event type: {}", eventType);

        try {
            if ("payment_intent.succeeded".equals(eventType)) {
                PaymentIntent eventIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
                if (eventIntent != null) {
                    PaymentIntent intent = stripeService.retrievePaymentIntent(eventIntent.getId());
                    metadata = intent.getMetadata();
                    userId = metadata.get("userId");
                    amountInDollars = new BigDecimal(intent.getAmount()).divide(new BigDecimal(100));
                    log.info("Retrieved PaymentIntent Metadata: {}", metadata);
                }
            } else if ("charge.succeeded".equals(eventType)) {
                Charge charge = (Charge) event.getDataObjectDeserializer().getObject().orElse(null);
                if (charge != null && charge.getPaymentIntent() != null) {
                    PaymentIntent intent = stripeService.retrievePaymentIntent(charge.getPaymentIntent());
                    metadata = intent.getMetadata();
                    userId = metadata.get("userId");
                    amountInDollars = new BigDecimal(charge.getAmount()).divide(new BigDecimal(100));
                    log.info("Retrieved Parent PaymentIntent Metadata: {}", metadata);
                }
            }
        } catch (StripeException e) {
            log.error("Stripe API error during object retrieval: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error retrieving data from Stripe: " + e.getMessage());
        }

        if ("payment_intent.succeeded".equals(eventType) || "charge.succeeded".equals(eventType)) {
            if (userId == null || userId.isEmpty()) {
                log.error("ERROR: No userId found in {} metadata after retrieval!", eventType);
                String keys = metadata != null ? String.join(", ", metadata.keySet()) : "none";
                return ResponseEntity.ok("Received " + eventType + " but no userId. Keys found: " + keys);
            }

            log.info("Attempting to add ${} to user {}", amountInDollars, userId);
            try {
                walletService.addFunds(userId, amountInDollars);
                log.info("SUCCESS: Wallet updated for user {}", userId);
                return ResponseEntity.ok("Received " + eventType + " and updated balance for " + userId);
            } catch (Exception e) {
                log.error("DATABASE UPDATE FAILED for user {}: {}", userId, e.getMessage());
                return ResponseEntity.internalServerError().body("Wallet update failed: " + e.getMessage());
            }
        } else {
            log.info("Ignored event type: {}", eventType);
        }

        return ResponseEntity.ok("Received " + eventType);
    }
}
