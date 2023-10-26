package com.template.server.domain.order.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductOrderRequest {
    private String imageUrl;
    private Double totalPrice;
    private Integer quantity;
}
