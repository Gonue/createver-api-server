package com.template.server.domain.order.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductOrderRequest {
    private final String imageUrl;
    private final Double totalPrice;
    private final Integer quantity;
    private final String buyerAddress;
    private final String buyerPostcode;
    private final String buyerName;
}
