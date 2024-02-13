package com.createver.server.domain.banner.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BannerCreateRequest {

    @NotBlank(message = "이미지 URL은 필수입니다.")
    private String imageUrl;

    private List<String> tags;

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotNull(message = "활성 상태는 필수입니다.")
    private boolean active;

    @NotBlank(message = "위치는 필수입니다.")
    private String position;

    private int orderSequence;

}
