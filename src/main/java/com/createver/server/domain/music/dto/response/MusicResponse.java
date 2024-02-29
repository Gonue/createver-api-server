package com.createver.server.domain.music.dto.response;

import com.createver.server.domain.member.dto.response.MemberResponse;
import com.createver.server.domain.music.dto.MusicDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MusicResponse {

    private final Long musicId;
    private final String prompt;
    private final String resultMusicUrl;
    private final String status;
    private final LocalDateTime createdAt;
    private final MemberResponse member;

    public static MusicResponse from(MusicDto dto){
        return new MusicResponse(
                dto.getMusicId(),
                dto.getPrompt(),
                dto.getResultMusicUrl(),
                dto.getStatus(),
                dto.getCreatedAt(),
                MemberResponse.from(dto.getMember())
        );
    }
}
