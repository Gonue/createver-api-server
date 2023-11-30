package com.template.server.domain.music.dto;

import com.template.server.domain.member.dto.MemberDto;
import com.template.server.domain.music.entity.Album;
import com.template.server.global.util.aws.CloudFrontUrlUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AlbumDto {

    private final Long albumId;
    private final String title;
    private final String imageUrl;
    private final String musicURl;
    private final MemberDto member;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;


    public static AlbumDto from(Album entity) {
        String cloudFrontImageUrl = CloudFrontUrlUtils.convertToCloudFrontUrl(entity.getImageUrl());
        String cloudFrontMusicUrl = CloudFrontUrlUtils.convertToCloudFrontUrl(entity.getMusicUrl());
        return new AlbumDto(
                entity.getAlbumId(),
                entity.getTitle(),
                cloudFrontImageUrl,
                cloudFrontMusicUrl,
                MemberDto.from(entity.getMember()),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );

    }
}
