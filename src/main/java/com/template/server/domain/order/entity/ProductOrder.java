package com.template.server.domain.order.entity;

import com.template.server.domain.member.entity.Member;
import com.template.server.global.audit.AuditingFields;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Table(name = "product_order")
@Entity
public class ProductOrder extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_order_id", updatable = false)
    private Long productOrderId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProductOrderStatus productOrderStatus;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "quantity")
    private Integer quantity;

    public static ProductOrder of(Member member, String imageUrl, ProductOrderStatus productOrderStatus, Double totalPrice, Integer quantity) {
        ProductOrder entity = new ProductOrder();
        entity.setMember(member);
        entity.setImageUrl(imageUrl);
        entity.setProductOrderStatus(productOrderStatus);
        entity.setTotalPrice(totalPrice);
        entity.setQuantity(quantity);
        return entity;
    }
}
