package com.createver.server.domain.order.service;

import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.domain.order.dto.ProductOrderDto;
import com.createver.server.domain.order.dto.Request.ProductOrderRequest;
import com.createver.server.domain.order.entity.ProductOrder;
import com.createver.server.domain.order.entity.ProductOrderStatus;
import com.createver.server.domain.order.repository.ProductOrderRepository;
import com.createver.server.global.error.exception.BusinessLogicException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Product Order Service 테스트")
@ExtendWith(MockitoExtension.class)
class ProductOrderServiceTest {

    @InjectMocks
    private ProductOrderService productOrderService;

    @Mock
    private ProductOrderRepository productOrderRepository;

    @Mock
    private MemberRepository memberRepository;

    private Member member;
    private ProductOrderRequest request;
    private ProductOrder productOrder;

    @BeforeEach
    void setUp() {
        String email = "test@example.com";
        member = Member.builder().email(email).profileImage("url").build();
        request = new ProductOrderRequest("imageUrl", 1000.0, 1, "address", "postcode", "buyerName");
        productOrder = ProductOrder.of(
            member, request.getImageUrl(), ProductOrderStatus.PENDING, request.getTotalPrice(),
            request.getQuantity(), null, request.getBuyerAddress(),
            request.getBuyerPostcode(), request.getBuyerName(), "orderNumber"
        );
    }

    @DisplayName("주문 생성 기능 테스트")
    @Test
    void createOrderTest() {
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
        when(productOrderRepository.save(any(ProductOrder.class))).thenReturn(productOrder);

        productOrderService.createOrder(member.getEmail(), request);

        verify(productOrderRepository, times(1)).save(any(ProductOrder.class));
    }

    @DisplayName("주문 상태 업데이트 기능 테스트")
    @Test
    void updateOrderStatusTest() {
        Long orderId = 1L;
        when(productOrderRepository.findById(orderId)).thenReturn(Optional.of(productOrder));
        when(productOrderRepository.save(any(ProductOrder.class))).thenReturn(productOrder);

        ProductOrderDto updatedOrder = productOrderService.updateOrderStatus(orderId, ProductOrderStatus.DELIVERED);

        assertEquals(ProductOrderStatus.DELIVERED, updatedOrder.getProductOrderStatus());
    }

    @DisplayName("특정 이메일 주문 조회 기능 테스트")
    @Test
    void getOrdersByEmailTest() {
        Pageable pageable = mock(Pageable.class);
        Page<ProductOrder> page = new PageImpl<>(Collections.singletonList(productOrder));

        when(productOrderRepository.findByMemberEmail(member.getEmail(), pageable)).thenReturn(page);

        Page<ProductOrderDto> orders = productOrderService.getOrdersByEmail(member.getEmail(), pageable);

        assertFalse(orders.isEmpty());
    }

    @DisplayName("회원 찾기 실패 시 예외 발생 테스트")
    @Test
    void createOrder_MemberNotFound_ThrowsException() {
        String email = "nonexistent@example.com";
        ProductOrderRequest request = new ProductOrderRequest("imageUrl", 1000.0, 1, "address", "postcode", "buyerName");

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(BusinessLogicException.class, () -> productOrderService.createOrder(email, request));
    }

    @DisplayName("주문 찾기 실패 시 예외 발생 테스트")
    @Test
    void updateOrderStatus_OrderNotFound_ThrowsException() {
        Long nonExistentOrderId = 999L;

        when(productOrderRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());

        assertThrows(BusinessLogicException.class, () -> productOrderService.updateOrderStatus(nonExistentOrderId, ProductOrderStatus.DELIVERED));
    }
}