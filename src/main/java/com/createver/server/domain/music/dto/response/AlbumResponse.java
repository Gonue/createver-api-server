package com.createver.server.domain.music.dto.response;

import com.createver.server.domain.member.dto.response.MemberResponse;
import com.createver.server.domain.music.dto.AlbumDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AlbumResponse {

    private final Long albumId;
    private final String title;
    private final String imageUrl;
    private final String musicUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
    private final MemberResponse member;

    public static AlbumResponse from(AlbumDto dto){
        return new AlbumResponse(
                dto.getAlbumId(),
                dto.getTitle(),
                dto.getImageUrl(),
                dto.getMusicURl(),
                dto.getCreatedAt(),
                dto.getModifiedAt(),
                MemberResponse.from(dto.getMember())
        );
    }
}
