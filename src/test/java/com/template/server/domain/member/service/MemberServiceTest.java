package com.template.server.domain.member.service;

import com.template.server.domain.image.dto.GalleryDto;
import com.template.server.domain.image.entity.Gallery;
import com.template.server.domain.image.repository.GalleryRepository;
import com.template.server.domain.member.dto.MemberDto;
import com.template.server.domain.member.entity.Member;
import com.template.server.domain.member.repository.MemberRepository;
import com.template.server.global.auth.utils.CustomAuthorityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MemberServiceTest {
    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CustomAuthorityUtils customAuthorityUtils;

    @Mock
    private GalleryRepository galleryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testJoin() {
        // Given
        String email = "test@test.com";
        String password = "password";
        String nickName = "nickName";
        String encodedPassword = "encodedPassword";

        Member member = Member.of(email, nickName, encodedPassword);

        // When
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // Then
        MemberDto result = memberService.join(email, password, nickName);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals(nickName, result.getNickName());
    }

    @Test
    void testOauthJoin() {
        // Given
        String email = "test@test.com";
        String nickName = "nickName";
        String randomPassword = UUID.randomUUID().toString();
        String encodedPassword = "encodedPassword";

        // When
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any(String.class))).thenReturn(encodedPassword);
        when(memberRepository.save(any(Member.class))).thenReturn(Member.of(email, nickName, encodedPassword));

        // Then
        assertDoesNotThrow(() -> memberService.oauthJoin(email, nickName));
    }

    @Test
    void testGetMemberInfo() {
        // Given
        String email = "test@test.com";
        Member member = Member.of(email, "nickName", "encodedPassword");

        // When
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        // Then
        MemberDto result = memberService.getMemberInfo(email);
        assertNotNull(result);
    }

    @Test
    void testUpdate() {
        // Given
        String email = "test@test.com";
        Optional<String> nickName = Optional.of("newNickName");
        Optional<String> profileImage = Optional.of("newProfileImage");
        Member member = Member.of(email, "oldNickName", "encodedPassword");

        // When
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // Then
        MemberDto result = memberService.update(email, nickName, profileImage);
        assertNotNull(result);
    }

    @Test
    void testDelete() {
        // Given
        String email = "test@test.com";
        Member member = Member.of(email, "nickName", "encodedPassword");

        // When
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        // Then
        assertDoesNotThrow(() -> memberService.delete(email));
    }

    @Test
    void testGetMyGalleries() {
        // Given
        String email = "test@test.com";
        Pageable pageable = PageRequest.of(0, 10);
        List<Gallery> galleryList = Arrays.asList(
                Gallery.create("prompt1", "url1", 1),
                Gallery.create("prompt2", "url2", 2)
        );
        Page<Gallery> galleryPage = new PageImpl<>(galleryList, pageable, galleryList.size());

        // When
        when(galleryRepository.findByMemberEmail(email, pageable)).thenReturn(galleryPage);

        // Then
        Page<GalleryDto> result = memberService.getMyGalleries(email, pageable);
        assertNotNull(result);
        assertEquals(galleryList.size(), result.getContent().size());
    }
}
