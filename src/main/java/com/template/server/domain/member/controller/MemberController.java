package com.template.server.domain.member.controller;

import com.template.server.domain.member.dto.MemberDto;
import com.template.server.domain.member.dto.request.MemberJoinRequest;
import com.template.server.domain.member.dto.request.MemberUpdateRequest;
import com.template.server.domain.member.dto.response.MemberJoinResponse;
import com.template.server.domain.member.dto.response.MemberResponse;
import com.template.server.domain.member.service.MemberService;
import com.template.server.global.error.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    //회원가입
    @PostMapping("/join")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<MemberJoinResponse> join(@Valid @RequestBody MemberJoinRequest request){
        MemberDto memberDto = memberService.join(request.getEmail(), request.getPassword(), request.getNickname());
        return Response.success(201, MemberJoinResponse.from(memberDto));
    }

    //회원조회
    @GetMapping("/info")
    public Response<MemberResponse> getMemberInfo(Authentication authentication){
        MemberDto memberDto = memberService.getMemberInfo(authentication.getName());
        return Response.success(200, MemberResponse.from(memberDto));
    }

    //프로필, 닉네임 변경
    @PatchMapping("/update")
    public Response<MemberResponse> update(@RequestBody MemberUpdateRequest request, Authentication authentication){
        MemberDto memberDto = memberService.update(
                authentication.getName(),
                Optional.ofNullable(request.getNickName()),
                Optional.ofNullable(request.getProfileImage()));
        return Response.success(200, MemberResponse.from(memberDto));
    }

    //회원 탈퇴
    @DeleteMapping("/exit")
    public Response<Void> delete(Authentication authentication){
        memberService.delete(authentication.getName());
        return Response.success();
    }

}
