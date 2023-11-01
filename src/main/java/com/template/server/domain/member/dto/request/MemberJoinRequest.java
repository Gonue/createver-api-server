package com.template.server.domain.member.dto.request;

import com.template.server.global.validator.PasswordMatches;
import com.template.server.global.validator.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
