package com.template.server.domain.order.dto.Response;

import com.template.server.domain.member.dto.response.MemberResponse;
import com.template.server.domain.order.dto.ProductOrderDto;
import com.template.server.domain.order.entity.ProductOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProductOrderResponse {

    private final Long productOrderId;
    private final MemberResponse member;
    private final String imageUrl;
    private final ProductOrderStatus productOrderStatus;
    private final Double totalPrice;
    private final Integer quantity;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static ProductOrderResponse from(ProductOrderDto dto){
        return new ProductOrderResponse(
                dto.getProductOrderId(),
                MemberResponse.from(dto.getMember()),
                dto.getImageUrl(),
                dto.getProductOrderStatus(),
                dto.getTotalPrice(),
                dto.getQuantity(),
                dto.getCreatedAt(),
                dto.getModifiedAt()
        );
    }
}
