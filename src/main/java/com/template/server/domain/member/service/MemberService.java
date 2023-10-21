package com.template.server.domain.member.service;

import com.template.server.domain.image.dto.GalleryDto;
import com.template.server.domain.image.entity.Gallery;
import com.template.server.domain.image.repository.GalleryRepository;
import com.template.server.domain.member.dto.MemberDto;
import com.template.server.domain.member.entity.Member;
import com.template.server.domain.member.repository.MemberRepository;
import com.template.server.global.auth.utils.CustomAuthorityUtils;
import com.template.server.global.error.exception.BusinessLogicException;
import com.template.server.global.error.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthorityUtils customAuthorityUtils;
    private final GalleryRepository galleryRepository;

    //회원가입
    @Transactional
    public MemberDto join(String email, String password, String nickName) {
        memberRepository.findByEmail(email).ifPresent(it -> {
            throw new BusinessLogicException(ExceptionCode.DUPLICATED_EMAIL, String.format("%s 는 이미 존재하는 이메일입니다.", email));
        });
        Member savedMember = memberRepository.save(Member.of(email, nickName, passwordEncoder.encode(password)));
        defaultImageSet(savedMember);
        setRoles(savedMember, email);
        return MemberDto.from(savedMember);
    }

    //Oauth
    public void oauthJoin(String email, String nickName) {
        Member savedMember = memberRepository.findByEmail(email)
                                             .orElseGet(() -> {
                                                 String password = UUID.randomUUID().toString();
                                                 return memberRepository.save(Member.of(email, nickName, passwordEncoder.encode(password)));
                                             });
        defaultImageSet(savedMember);
        setRoles(savedMember, email);
        memberRepository.save(savedMember);
        MemberDto.from(savedMember);
    }


    //회원 조회
    public MemberDto getMemberInfo(String email) {
        Member member = memberOrException(email);
        return MemberDto.from(member);
    }

    //프로필, 닉네임 변경
    @Transactional
    public MemberDto update(String email, Optional<String> nickName, Optional<String> profileImage) {
        Member member = memberOrException(email);
        if (profileImage.isPresent() && !profileImage.get().isEmpty()) {
            member.setProfileImage(profileImage.get());
        }
        if (nickName.isPresent() && !nickName.get().isEmpty()) {
            member.setNickName(nickName.get());
        }
        return MemberDto.from(memberRepository.save(member));
    }

    @Transactional
    //회원 탈퇴
    public void delete(String email) {
        Member member = memberOrException(email);
        memberRepository.delete(member);
    }

    //사용자가 생성한 이미지 목록
    public Page<GalleryDto> getMyGalleries(String email, Pageable pageable){
        Page<Gallery> galleryPage = galleryRepository.findByMemberEmail(email, pageable);
        return galleryPage.map(GalleryDto::from);
    }


    private Member memberOrException(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND, String.format("%s 를 찾을 수 없습니다.", email)));
    }
    private void defaultImageSet(Member member){
         if (member.getProfileImage() == null || member.getProfileImage().isEmpty()){
             member.setProfileImage("https://github.com/Gonue/mine/assets/109960034/c095e470-f43b-4bcf-934d-d567d78605bb");
         }
     }
    private void setRoles(Member member, String email){
        List<String> roles = customAuthorityUtils.createRoles(email);
        member.setRoles(roles);
    }

}
