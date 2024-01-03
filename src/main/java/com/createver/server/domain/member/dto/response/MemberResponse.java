package com.createver.server.domain.member.dto.response;

import com.createver.server.domain.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponse {

    private Long memberId;
    private String email;
    private String nickName;
    private String profileImage;
    private String planType;
    private boolean isOauthUser;

    public static MemberResponse from(MemberDto dto){
        return new MemberResponse(
                dto.getMemberId(),
                dto.getEmail(),
                dto.getNickName(),
                dto.getProfileImage(),
                dto.getPlanType() != null ? dto.getPlanType().toString() : "Free",
                dto.isOauthUser()
        );
    }
}
