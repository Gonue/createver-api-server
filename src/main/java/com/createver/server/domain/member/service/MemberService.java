package com.createver.server.domain.member.service;

import com.createver.server.domain.image.dto.GalleryDto;
import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.image.repository.gallery.GalleryRepository;
import com.createver.server.domain.member.dto.MemberDto;
import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.global.auth.utils.CustomAuthorityUtils;
import com.createver.server.global.error.exception.BusinessLogicException;
import com.createver.server.global.error.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthorityUtils customAuthorityUtils;
    private final GalleryRepository galleryRepository;


    public static final String DEFAULT_IMAGE = "https://d2xbqs28wc0ywi.cloudfront.net/images/65fc23b3-e151-47cc-9c9b-1af57381e6fa_ham1.png";

    @Transactional
    public MemberDto join(String email, String password, String nickName) {
        memberRepository.findByEmail(email).ifPresent(it -> {
            throw new BusinessLogicException(ExceptionCode.DUPLICATED_EMAIL, String.format("%s 는 이미 존재하는 이메일입니다.", email));
        });
        Member savedMember = memberRepository.save(Member.builder()
                .email(email)
                .nickName(nickName)
                .password(passwordEncoder.encode(password))
                .profileImage(DEFAULT_IMAGE)
                .roles(customAuthorityUtils.createRoles(email))
                .build());
        return MemberDto.from(savedMember);
    }

    //Oauth
    public void oauthJoin(String email, String nickName) {
        memberRepository.findByEmail(email).orElseGet(() -> {
            String password = UUID.randomUUID().toString();
            return memberRepository.save(Member.builder()
                    .email(email)
                    .nickName(nickName)
                    .password(passwordEncoder.encode(password))
                    .profileImage(DEFAULT_IMAGE)
                    .roles(customAuthorityUtils.createRoles(email))
                    .build());
        });
    }

    //회원 조회
    @Transactional(readOnly = true)
    @Cacheable(value = "MemberCacheStore", key = "#email", cacheManager = "cacheManager", unless = "#result == null")
    public MemberDto getMemberInfo(String email) {
        Member member = memberOrException(email);
        return MemberDto.from(member);
    }

    //프로필, 닉네임 변경
    @Transactional
    @CachePut(value = "MemberCacheStore", key = "#email", cacheManager = "cacheManager")
    public MemberDto update(String email, Optional<String> nickName, Optional<String> profileImage) {
        Member member = memberOrException(email);
        member.updateMemberInfo(nickName.orElse(null), profileImage.orElse(null));
        return MemberDto.from(memberRepository.save(member));
    }


    //회원 탈퇴
    @Transactional
    @CacheEvict(value = "MemberCacheStore", key = "#email", cacheManager = "cacheManager")
    public void delete(String email) {
        Member member = memberOrException(email);
        memberRepository.delete(member);
    }

    //사용자가 생성한 이미지 목록
    @Transactional(readOnly = true)
    public Page<GalleryDto> getMyGalleries(String email, Pageable pageable) {
        Page<Gallery> galleryPage = galleryRepository.findGalleryByMemberEmail(email, pageable);
        return galleryPage.map(GalleryDto::from);
    }


    private Member memberOrException(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND, String.format("%s 를 찾을 수 없습니다.", email)));
    }
}
