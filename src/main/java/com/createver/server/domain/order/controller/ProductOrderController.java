package com.createver.server.domain.order.controller;

import com.createver.server.domain.order.dto.ProductOrderDto;
import com.createver.server.domain.order.dto.Request.ProductOrderRequest;
import com.createver.server.domain.order.dto.Response.ProductOrderResponse;
import com.createver.server.domain.order.entity.ProductOrderStatus;
import com.createver.server.domain.order.service.ProductOrderService;
import com.createver.server.global.error.response.Response;
import jakarta.validation.Valid;
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
    public Response<Void> createOrder(@RequestBody @Valid ProductOrderRequest request,
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
