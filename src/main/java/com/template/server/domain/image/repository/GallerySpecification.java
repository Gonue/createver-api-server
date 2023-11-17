package com.template.server.domain.image.repository;

import com.template.server.domain.image.entity.Gallery;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class GallerySpecification {

    public static Specification<Gallery> hasOptionsAndPrompt(List<Integer> options, String prompt) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (options != null && !options.isEmpty()) {
                CriteriaBuilder.In<Integer> inClause = criteriaBuilder.in(root.get("option"));
                for (Integer option : options) {
                    inClause.value(option);
                }
                predicates.add(inClause);
            }

            if (prompt != null && !prompt.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("prompt"), "%" + prompt + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}


