package com.bidly.coreservice.dto.wallet;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletRechargeDTO {

    @NotNull
    @DecimalMin(value = "0.01", message = "Recharge amount must be greater than 0")
    private BigDecimal amount;
}