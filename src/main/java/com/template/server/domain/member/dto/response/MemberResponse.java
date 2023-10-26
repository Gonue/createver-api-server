package com.template.server.domain.member.dto.response;

import com.template.server.domain.member.dto.MemberDto;
import com.template.server.domain.member.entity.PlanType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
public class MemberResponse {

    private Long memberId;
    private String email;
    private String nickName;
    private String profileImage;
    private String planType;

    public static MemberResponse from(MemberDto dto){
        return new MemberResponse(
                dto.getMemberId(),
                dto.getEmail(),
                dto.getNickName(),
                dto.getProfileImage(),
                dto.getPlanType() != null ? dto.getPlanType().toString() : "Free"
        );
    }
}
