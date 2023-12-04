package com.createver.server.domain.music.dto;

import com.createver.server.domain.member.dto.MemberDto;
import com.createver.server.domain.music.entity.Album;
import com.createver.server.global.util.aws.CloudFrontUrlUtils;
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
