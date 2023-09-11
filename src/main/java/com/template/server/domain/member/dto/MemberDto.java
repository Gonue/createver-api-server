package com.template.server.domain.member.dto;

import com.template.server.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MemberDto {
    private Long memberId;
    private String email;
    private String nickName;
    private String password;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static MemberDto from(Member entity){
        return new MemberDto(
                entity.getMemberId(),
                entity.getEmail(),
                entity.getNickName(),
                entity.getPassword(),
                entity.getProfileImage(),
                entity.getCreatedAt(),
                entity.getModifiedAt()
        );
    }
}
