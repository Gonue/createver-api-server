package com.template.server.domain.member.dto;

import com.template.server.domain.member.entity.Member;
import com.template.server.domain.member.entity.PlanType;
import com.template.server.global.util.CloudFrontUrlUtils;
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
    private PlanType planType;

    public static MemberDto from(Member entity){
        String cloudFrontProfileImageUrl = CloudFrontUrlUtils.convertToCloudFrontUrl(entity.getProfileImage());
        return new MemberDto(
                entity.getMemberId(),
                entity.getEmail(),
                entity.getNickName(),
                entity.getPassword(),
                cloudFrontProfileImageUrl,
                entity.getCreatedAt(),
                entity.getModifiedAt(),
                entity.getPlan() != null ? entity.getPlan().getPlanType() : null
        );
    }
}
