package com.createver.server.domain.music.dto;

import com.createver.server.domain.member.dto.MemberDto;
import com.createver.server.domain.music.entity.Music;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MusicDto {

    private final Long musicId;
    private final String prompt;
    private final String resultMusicUrl;
    private final String status;
    private final String predictionId;
    private final MemberDto member;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static MusicDto from(Music entity){
        return new MusicDto(
                entity.getMusicId(),
                entity.getPrompt(),
                entity.getResultMusicUrl(),
                entity.getStatus(),
                entity.getPredictionId(),
                MemberDto.from(entity.getMember()),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }
}
