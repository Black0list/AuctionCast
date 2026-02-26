package com.bidly.coreservice.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShipOrderDTO {

    @NotBlank
    private String carrier;

    @NotBlank
    private String trackingNumber;
}