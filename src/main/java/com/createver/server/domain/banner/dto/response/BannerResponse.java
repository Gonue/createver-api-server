package com.createver.server.domain.banner.dto.response;

import com.createver.server.domain.banner.dto.BannerDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class BannerResponse {

    private final Long bannerId;
    private final String imageUrl;
    private final List<String> tags;
    private final String title;
    private final String content;
    private final boolean active;
    private final String position;
    private final int orderSequence;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static BannerResponse from(BannerDto dto){
        return new BannerResponse(
                dto.getBannerId(),
                dto.getImageUrl(),
                dto.getTags(),
                dto.getTitle(),
                dto.getContent(),
                dto.isActive(),
                dto.getPosition(),
                dto.getOrderSequence(),
                dto.getCreatedAt(),
                dto.getModifiedAt()
        );
    }
}
