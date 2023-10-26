package com.template.server.domain.order.service;

import com.template.server.domain.member.entity.Member;
import com.template.server.domain.member.repository.MemberRepository;
import com.template.server.domain.order.dto.ProductOrderDto;
import com.template.server.domain.order.entity.ProductOrder;
import com.template.server.domain.order.entity.ProductOrderStatus;
import com.template.server.domain.order.repository.ProductOrderRepository;
import com.template.server.global.error.exception.BusinessLogicException;
import com.template.server.global.error.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductOrderService {

    private final ProductOrderRepository productOrderRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createOrder(String email, String imageUrl, Double totalPrice, Integer quantity){
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND, String.format("%s 을 찾을수 없음", email)));
        ProductOrder productOrder = ProductOrder.of(member, imageUrl, ProductOrderStatus.PENDING, totalPrice, quantity);
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
}
