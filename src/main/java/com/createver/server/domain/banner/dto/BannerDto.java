package com.createver.server.domain.banner.dto;

import com.createver.server.domain.banner.entity.Banner;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BannerDto {

    private Long bannerId;
    private String imageUrl;
    private List<String> tags;
    private String title;
    private String content;
    private boolean active;
    private String position;
    private int orderSequence;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static BannerDto from(Banner entity){
        return new BannerDto(
                entity.getBannerId(),
                entity.getImageUrl(),
                entity.getTags(),
                entity.getTitle(),
                entity.getContent(),
                entity.isActive(),
                entity.getPosition(),
                entity.getOrderSequence(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }
}
