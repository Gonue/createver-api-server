package com.createver.server.domain.member.dto;

import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.entity.PlanType;
import com.createver.server.global.util.aws.CloudFrontUrlUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberDto {
    private Long memberId;
    private String email;
    private String nickName;
    private String password;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private PlanType planType;
    private boolean isOauthUser;

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
                entity.getPlan() != null ? entity.getPlan().getPlanType() : null,
                entity.isOauthUser()
        );
    }
}
