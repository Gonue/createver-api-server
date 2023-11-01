package com.template.server.domain.member.entity;

import com.template.server.global.audit.AuditingFields;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Setter @Enumerated(EnumType.STRING)
    @Column(name = "plan_type")
    private PlanType planType;

    @Setter @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @Setter @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    public static Plan create(PlanType planType, LocalDateTime purchaseDate, LocalDateTime expiryDate) {
        Plan plan = new Plan();
        plan.setPlanType(planType);
        plan.setPurchaseDate(purchaseDate);
        plan.setExpiryDate(expiryDate);
        return plan;
    }
}
