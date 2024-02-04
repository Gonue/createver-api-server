package com.createver.server.domain.member.service;

import com.createver.server.domain.image.dto.GalleryDto;
import com.createver.server.domain.image.dto.ImageAvatarDto;
import com.createver.server.domain.image.entity.Gallery;
import com.createver.server.domain.image.entity.ImageAvatar;
import com.createver.server.domain.image.repository.avatar.ImageAvatarRepository;
import com.createver.server.domain.image.repository.gallery.GalleryRepository;
import com.createver.server.domain.member.dto.MemberDto;
import com.createver.server.domain.member.entity.Member;
import com.createver.server.domain.member.repository.MemberRepository;
import com.createver.server.global.auth.utils.CustomAuthorityUtils;
import com.createver.server.global.error.exception.BusinessLogicException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Member Service 테스트")
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

    @Mock
    private ImageAvatarRepository imageAvatarRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원가입 테스트")
    void testJoin() {
        // Given
        String email = "test@test.com";
        String password = "password";
        String nickName = "nickName";
        String encodedPassword = "encodedPassword";

        Member member = Member.builder()
                .email(email)
                .nickName(nickName)
                .password(encodedPassword)
                .profileImage(MemberService.DEFAULT_IMAGE)
                .build();

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
    @DisplayName("OAuth 회원가입 테스트")
    void testOauthJoin() {
        // Given
        String email = "test@test.com";
        String nickName = "nickName";
        String encodedPassword = "encodedPassword";

        // When
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any(String.class))).thenReturn(encodedPassword);
        when(memberRepository.save(any(Member.class))).thenReturn(Member.builder()
                .email(email)
                .nickName(nickName)
                .password(encodedPassword)
                .build());

        // Then
        assertDoesNotThrow(() -> memberService.oauthJoin(email, nickName));
    }

    @Test
    @DisplayName("이메일 중복 시 회원가입 테스트")
    void testJoin_WithDuplicatedEmail() {
        // Given
        String email = "test@test.com";
        String password = "password";
        String nickName = "nickName";

        Member existingMember = Member.builder()
                .email(email)
                .password("existingPassword")
                .nickName("existingNickName")
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(existingMember));

        // Then
        assertThrows(BusinessLogicException.class, () -> {
            // When
            memberService.join(email, password, nickName);
        });
    }

    @Test
    @DisplayName("회원 정보 조회 테스트")
    void testGetMemberInfo() {
        // Given
        String email = "test@test.com";
        String nickName = "nickName";
        String encodedPassword = "encodedPassword";

        Member member = Member.builder()
                .email(email)
                .nickName(nickName)
                .password(encodedPassword)
                .profileImage("url")
                .build();

        // When
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));

        // Then
        MemberDto result = memberService.getMemberInfo(email);
        assertNotNull(result);
    }

    @Test
    @DisplayName("회원 정보 업데이트 테스트")
    void testUpdate() {
        // Given
        String email = "test@test.com";
        Optional<String> nickName = Optional.of("newNickName");
        Optional<String> profileImage = Optional.of("newProfileImage");
        Member member = Member.builder()
                .email(email)
                .nickName("oldNickName")
                .password("encodedPassword")
                .build();

        // When
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        // Then
        MemberDto result = memberService.update(email, nickName, profileImage);
        assertNotNull(result);
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    void testDelete() {
        // Given
        String email = "test@test.com";
        String rawPassword = "rawPassword";
        String encodedPassword = "encodedPassword";
        Member member = Member.builder()
                .email(email)
                .password(encodedPassword)
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        // When & Then
        assertDoesNotThrow(() -> memberService.delete(email, rawPassword));
    }

    @Test
    @DisplayName("잘못된 비밀번호로 회원 탈퇴 시도 테스트")
    void testDelete_WithInvalidPassword() {
        // Given
        String email = "test@test.com";
        String rawPassword = "rawPassword";
        Member member = Member.builder()
                .email(email)
                .password("encodedPassword")
                .build();

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(rawPassword, member.getPassword())).thenReturn(false);

        // Then
        assertThrows(BusinessLogicException.class, () -> {
            // When
            memberService.delete(email, rawPassword);
        });
    }

    @Test
    @DisplayName("회원의 갤러리 목록 조회 테스트")
    void testGetMyGalleries() {
        // Given
        String email = "test@test.com";
        Pageable pageable = PageRequest.of(0, 10);
        List<Gallery> galleryList = Arrays.asList(
                Gallery.builder()
                        .prompt("prompt1")
                        .storageUrl("url1")
                        .option(1)
                        .build(),
                Gallery.builder()
                        .prompt("prompt2")
                        .storageUrl("url2")
                        .option(2)
                        .build()
        );
        Page<Gallery> galleryPage = new PageImpl<>(galleryList, pageable, galleryList.size());

        // When
        when(galleryRepository.findGalleryByMemberEmail(email, pageable)).thenReturn(galleryPage);

        // Then
        Page<GalleryDto> result = memberService.getMyGalleries(email, pageable);
        assertNotNull(result);
        assertEquals(galleryList.size(), result.getContent().size());
    }

    @Test
    @DisplayName("회원의 아바타 생성 목록 조회 테스트")
    void testGetMyAvatars() {
        // Given
        String email = "test@test.com";
        Pageable pageable = PageRequest.of(0, 10);

        Member member = Member.builder()
                .email(email)
                .nickName("nickName")
                .profileImage(MemberService.DEFAULT_IMAGE)
                .build();
        List<ImageAvatar> imageAvatarList = Arrays.asList(

                ImageAvatar.builder()
                        .prompt("Test Prompt")
                        .numSteps(50)
                        .styleName("Test Style")
                        .inputImage("https://www.example.com")
                        .numOutputs(5)
                        .guidanceScale(50)
                        .negativePrompt("Test Prompt")
                        .styleStrengthRatio(3)
                        .resultImageUrl("https://www.example.com")
                        .status("su")
                        .predictionId("Test ID")
                        .member(member)
                        .build()
        );
        Page<ImageAvatar> imageAvatarPage = new PageImpl<>(imageAvatarList, pageable, imageAvatarList.size());

        // When
        when(imageAvatarRepository.findByMemberEmail(email, pageable)).thenReturn(imageAvatarPage);

        // Then
        Page<ImageAvatarDto> result = memberService.getMyAvatar(email, pageable);
        assertThat(result).isNotNull();
        assertNotNull(result);
        assertEquals(imageAvatarList.size(), result.getContent().size());
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 회원 정보 조회 테스트")
    void testGetMemberInfo_WithNonExistentEmail() {
        // Given
        String email = "nonexistent@test.com";

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Then
        assertThrows(BusinessLogicException.class, () -> {
            // When
            memberService.getMemberInfo(email);
        });
    }
}
