package com.template.server.domain.order.entity;

import com.template.server.domain.member.entity.Member;
import com.template.server.global.audit.AuditingFields;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Table(name = "product_order")
@Entity
public class ProductOrder extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_order_id", updatable = false)
    private Long productOrderId; // 주문 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 회원 정보

    @Column(name = "image_url")
    private String imageUrl; // 상품 이미지 URL

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProductOrderStatus productOrderStatus; // 주문 상태

    @Column(name = "total_price")
    private Double totalPrice; // 총 가격

    @Column(name = "quantity")
    private Integer quantity; // 수량

    @Column(name = "payment_method")
    private String paymentMethod; // 결제 방법

    @Column(name = "order_number")
    private String orderNumber; // 주문 번호

    @Column(name = "buyer_address")
    private String buyerAddress; // 구매자 주소

    @Column(name = "buyer_postcode")
    private String buyerPostcode; // 구매자 우편번호

    @Column(name = "buyer_name")
    private String buyerName; // 구매자 이름

    public static ProductOrder of(Member member, String imageUrl, ProductOrderStatus productOrderStatus,
                                      Double totalPrice, Integer quantity, String paymentMethod,
                                      String buyerAddress, String buyerPostcode, String buyerName, String orderNumber) {
            ProductOrder entity = new ProductOrder();
            entity.setMember(member);
            entity.setImageUrl(imageUrl);
            entity.setProductOrderStatus(productOrderStatus);
            entity.setTotalPrice(totalPrice);
            entity.setQuantity(quantity);
            entity.setPaymentMethod(paymentMethod);
            entity.setBuyerAddress(buyerAddress);
            entity.setBuyerPostcode(buyerPostcode);
            entity.setBuyerName(buyerName);
            entity.setOrderNumber(orderNumber);
            return entity;
        }
}
