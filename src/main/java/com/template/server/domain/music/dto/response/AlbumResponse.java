package com.template.server.domain.music.dto.response;

import com.template.server.domain.member.dto.response.MemberResponse;
import com.template.server.domain.music.dto.AlbumDto;
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
    private final MemberResponse memberResponse;


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
