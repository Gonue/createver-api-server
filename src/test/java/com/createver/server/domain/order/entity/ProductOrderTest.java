package com.createver.server.domain.order.entity;

import com.createver.server.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("ProductOrder Entity 테스트")
class ProductOrderTest {

    @DisplayName("ProductOrder 생성 테스트")
    @Test
    void testProductOrderCreation() {
        // Given
        Member mockMember = mock(Member.class);
        String imageUrl = "image.jpg";
        ProductOrderStatus productOrderStatus = ProductOrderStatus.PENDING;
        Double totalPrice = 100.0;
        Integer quantity = 2;
        String paymentMethod = "Credit Card";
        String buyerAddress = "123 Street";
        String buyerPostcode = "123456";
        String buyerName = "John Doe";
        String orderNumber = "ORD123456";

        // When
        ProductOrder productOrder = ProductOrder.of(mockMember, imageUrl, productOrderStatus,
                                                     totalPrice, quantity, paymentMethod,
                                                     buyerAddress, buyerPostcode, buyerName, orderNumber);

        // Then
        assertEquals(mockMember, productOrder.getMember());
        assertEquals(imageUrl, productOrder.getImageUrl());
        assertEquals(productOrderStatus, productOrder.getProductOrderStatus());
        assertEquals(totalPrice, productOrder.getTotalPrice());
        assertEquals(quantity, productOrder.getQuantity());
        assertEquals(paymentMethod, productOrder.getPaymentMethod());
        assertEquals(buyerAddress, productOrder.getBuyerAddress());
        assertEquals(buyerPostcode, productOrder.getBuyerPostcode());
        assertEquals(buyerName, productOrder.getBuyerName());
        assertEquals(orderNumber, productOrder.getOrderNumber());
    }
}