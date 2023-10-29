package com.template.server.domain.order.repository;

import com.template.server.domain.member.entity.Member;
import com.template.server.domain.order.entity.ProductOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {
    Page<ProductOrder> findByMemberEmail(String email, Pageable pageable);
}
