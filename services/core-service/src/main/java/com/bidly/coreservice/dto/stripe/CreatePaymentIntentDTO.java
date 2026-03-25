package com.bidly.coreservice.dto.stripe;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePaymentIntentDTO {
    @NotNull
    @DecimalMin(value = "1.00", message = "Minimum recharge amount is $1.00")
    private BigDecimal amount;
}
