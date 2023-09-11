package com.template.server.domain.member.service;

import com.template.server.domain.member.dto.MemberDto;
import com.template.server.domain.member.entity.Member;
import com.template.server.domain.member.repository.MemberRepository;
import com.template.server.global.auth.utils.CustomAuthorityUtils;
import com.template.server.global.error.exception.BusinessLogicException;
import com.template.server.global.error.exception.ExceptionCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthorityUtils customAuthorityUtils;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, CustomAuthorityUtils customAuthorityUtils) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.customAuthorityUtils = customAuthorityUtils;
    }

    //회원가입
    @Transactional
    public MemberDto join(String email, String password, String nickName){
        memberRepository.findByEmail(email).ifPresent(it ->{
            throw new BusinessLogicException(ExceptionCode.DUPLICATED_EMAIL, String.format("%s 는 이미 존재하는 이메일입니다.", email));
        });
        Member savedMember = memberRepository.save(Member.of(email, nickName, passwordEncoder.encode(password)));
        List<String> roles = customAuthorityUtils.createRoles(email);
        savedMember.setRoles(roles);
        return MemberDto.from(savedMember);
    }

    //회원 조회
    public MemberDto getMemberInfo(String email){
        Member member = memberOrException(email);
        return MemberDto.from(member);
    }

    //프로필, 닉네임 변경
    @Transactional
    public MemberDto update(String email, Optional<String> nickName, Optional<String> profileImage){
        Member member = memberOrException(email);
        if (profileImage.isPresent() && !profileImage.get().isEmpty()){
            member.setProfileImage(profileImage.get());
        }
        if (nickName.isPresent() && !nickName.get().isEmpty()){
            member.setNickName(nickName.get());
        }
        return MemberDto.from(memberRepository.save(member));
    }

    @Transactional
    //회원 탈퇴
    public void delete(String email){
        Member member = memberOrException(email);
        memberRepository.delete(member);
    }

    private Member memberOrException(String email){
        return memberRepository.findByEmail(email).orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND, String.format("%s 를 찾을 수 없습니다.", email)));
    }
}
