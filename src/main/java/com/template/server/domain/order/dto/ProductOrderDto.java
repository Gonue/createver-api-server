package com.template.server.domain.order.dto;

import com.template.server.domain.member.dto.MemberDto;
import com.template.server.domain.order.entity.ProductOrder;
import com.template.server.domain.order.entity.ProductOrderStatus;
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
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }
}
