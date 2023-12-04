package com.createver.server.domain.order.dto;

import com.createver.server.domain.member.dto.MemberDto;
import com.createver.server.domain.order.entity.ProductOrder;
import com.createver.server.domain.order.entity.ProductOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductOrderDto {

    private final Long productOrderId;
    private final MemberDto member;
    private final String imageUrl;
    private final ProductOrderStatus productOrderStatus;
    private final Double totalPrice;
    private final Integer quantity;
    private final String paymentMethod;
    private final String buyerAddress;
    private final String buyerPostcode;
    private final String buyerName;
    private final String orderNumber;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static ProductOrderDto from(ProductOrder entity){
        return new ProductOrderDto(
                entity.getProductOrderId(),
                MemberDto.from(entity.getMember()),
                entity.getImageUrl(),
                entity.getProductOrderStatus(),
                entity.getTotalPrice(),
                entity.getQuantity(),
                entity.getPaymentMethod(),
                entity.getBuyerAddress(),
                entity.getBuyerPostcode(),
                entity.getBuyerName(),
                entity.getOrderNumber(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }
}
