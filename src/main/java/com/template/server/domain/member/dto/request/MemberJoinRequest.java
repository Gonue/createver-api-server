package com.template.server.domain.member.dto.request;

import com.template.server.global.validator.PasswordMatches;
import com.template.server.global.validator.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
@PasswordMatches
public class MemberJoinRequest {
    @Email
    private String email;
    @NotBlank
    private String nickname;
    @ValidPassword
    private String password;
    @NotBlank
    private String checkPassword;
}
