package com.createver.server.domain.member.dto.request;

import com.createver.server.global.validator.PasswordMatches;
import com.createver.server.global.validator.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

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
