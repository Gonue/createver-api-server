package com.template.server.domain.order.dto.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductOrderRequest {
    @NotBlank
    private final String imageUrl;
    @NotBlank
    private final Double totalPrice;
    @NotBlank
    private final Integer quantity;
    @NotBlank
    private final String buyerAddress;
    @NotBlank
    private final String buyerPostcode;
    @NotBlank
    private final String buyerName;
}
