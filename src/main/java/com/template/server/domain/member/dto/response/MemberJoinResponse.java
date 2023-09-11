package com.template.server.domain.member.dto.response;

import com.template.server.domain.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class MemberJoinResponse {
    private Long memberId;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;

    public static MemberJoinResponse from(MemberDto dto){
        return new MemberJoinResponse(
                dto.getMemberId(),
                dto.getEmail(),
                dto.getNickName(),
                dto.getCreatedAt()
        );
    }
}
