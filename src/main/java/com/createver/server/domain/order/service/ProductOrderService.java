package com.createver.server.domain.order.service;

import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.domain.order.dto.ProductOrderDto;
import com.createver.server.domain.order.dto.Request.ProductOrderRequest;
import com.createver.server.domain.order.entity.ProductOrder;
import com.createver.server.domain.order.entity.ProductOrderStatus;
import com.createver.server.domain.order.repository.ProductOrderRepository;
import com.createver.server.global.error.exception.BusinessLogicException;
import com.createver.server.global.error.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ProductOrderService {

    private final ProductOrderRepository productOrderRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createOrder(String email, ProductOrderRequest request) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND, String.format("%s 을 찾을수 없음", email)));

        String orderNumber = generateOrderNumber(); // 주문번호 생성 로직

        ProductOrder productOrder = ProductOrder.of(
                member,
                request.getImageUrl(),
                ProductOrderStatus.PENDING,
                request.getTotalPrice(),
                request.getQuantity(),
                null, // 결제방법은 나중에 업데이트
                request.getBuyerAddress(),
                request.getBuyerPostcode(),
                request.getBuyerName(),
                orderNumber
        );
        productOrderRepository.save(productOrder);
    }

    @Transactional
    public ProductOrderDto updateOrderStatus(Long orderId, ProductOrderStatus newStatus) {
        ProductOrder productOrder = productOrderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND, "주문을 찾을 수 없습니다."));

        productOrder.setProductOrderStatus(newStatus);
        return ProductOrderDto.from(productOrderRepository.save(productOrder));
    }

    @Transactional(readOnly = true)
    public Page<ProductOrderDto> getOrdersByEmail(String email, Pageable pageable) {
        Page<ProductOrder> orders = productOrderRepository.findByMemberEmail(email, pageable);
        return orders.map(ProductOrderDto::from);
    }



    private String generateOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedNow = now.format(formatter);

        Random random = new Random();
        int randomNum = 1000 + random.nextInt(9000);
        return String.format("ORD-%s-%04d", formattedNow, randomNum);
    }
}
