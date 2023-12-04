package com.createver.server.domain.member.entity;

import com.createver.server.global.audit.AuditingFields;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "plan")
@Entity
public class Plan extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type")
    private PlanType planType;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Builder
    public Plan(PlanType planType, LocalDateTime purchaseDate, LocalDateTime expiryDate){
        this.planType = planType;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;
    }
}
