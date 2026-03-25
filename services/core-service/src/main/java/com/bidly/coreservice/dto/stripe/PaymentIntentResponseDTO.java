package com.bidly.coreservice.dto.stripe;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentIntentResponseDTO {
    private String clientSecret;
    private String publishableKey;
}
