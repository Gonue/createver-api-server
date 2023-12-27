package com.createver.server.domain.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewCreateRequest {
    @NotNull
    @Min(0)
    @Max(5)
    private Double rating;
    @NotBlank
    private String content;
}
