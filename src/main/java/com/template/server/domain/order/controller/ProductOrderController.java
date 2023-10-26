package com.template.server.domain.order.controller;

import com.template.server.domain.order.dto.ProductOrderDto;
import com.template.server.domain.order.dto.Request.ProductOrderRequest;
import com.template.server.domain.order.dto.Response.ProductOrderResponse;
import com.template.server.domain.order.entity.ProductOrderStatus;
import com.template.server.domain.order.service.ProductOrderService;
import com.template.server.global.error.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class ProductOrderController {

    private final ProductOrderService productOrderService;

    // 주문 생성 API
    @PostMapping
    public Response<Void> createOrder(@RequestBody ProductOrderRequest request,
                                      Authentication authentication) {
        productOrderService.createOrder(authentication.getName(), request);
        return Response.success();
    }

    // 주문 상태 업데이트 API
    @PatchMapping("/{orderId}")
    public Response<ProductOrderResponse> updateOrderStatus(@PathVariable Long orderId,
                                                            @RequestParam ProductOrderStatus newStatus) {
        ProductOrderDto updatedOrder = productOrderService.updateOrderStatus(orderId, newStatus);
        return Response.success(200, ProductOrderResponse.from(updatedOrder));
    }

    // 이메일을 기반으로 한 주문 목록 조회 API
    @GetMapping
    public Response<Page<ProductOrderResponse>> getOrdersByEmail(Authentication authentication, Pageable pageable) {
        Page<ProductOrderDto> orders = productOrderService.getOrdersByEmail(authentication.getName(), pageable);
        return Response.success(orders.map(ProductOrderResponse::from));
    }
}
