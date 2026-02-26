package com.bidly.coreservice.dto.order;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrderDTO {

    @NotBlank
    private String shippingFullName;

    @NotBlank
    private String shippingPhone;

    @NotBlank
    private String shippingAddressLine1;

    private String shippingAddressLine2;

    @NotBlank
    private String shippingCity;

    private String shippingState;

    @NotBlank
    private String shippingPostalCode;

    @NotBlank
    private String shippingCountry;
}