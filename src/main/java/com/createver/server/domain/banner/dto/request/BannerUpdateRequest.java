package com.createver.server.domain.banner.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BannerUpdateRequest {

    private Long bannerId;
    private String imageUrl;
    private List<String> tags;
    private String title;
    private String content;
    private Boolean active;
    private String position;
    private Integer orderSequence;
}
